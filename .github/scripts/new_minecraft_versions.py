#!/usr/bin/env python3
"""
Weekly check for Minecraft releases that no jar covers yet (.github/workflows/new-minecraft-versions.yml).

For every Minecraft release newer than a loader's oldest supported version and missing from all of
that loader's mc_targets, it:
  1. checks that the loader has builds for it (Fabric API; NeoForge),
  2. adds it the way a person would: appends it to the mc_targets of the newest release target
     before it, and creates the <version>-<loader> smoke target,
  3. runs that smoke target's GameTests and production tests and rebuilds the release jar,
  4. pushes the changes that passed to the branch compat/new-minecraft-versions, and opens or updates
     one issue per Minecraft version, assigned to the repository owner (GitHub emails assignees).

Usage: new_minecraft_versions.py check   (needs GH_TOKEN, GITHUB_REPOSITORY, GITHUB_REPOSITORY_OWNER)
       new_minecraft_versions.py plan    (only prints what is new; no changes, no tests)
"""
import hashlib
import io
import json
import os
import re
import subprocess
import sys
import urllib.error
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
VERSIONS = ROOT / 'versions'
BRANCH = 'compat/new-minecraft-versions'
LOADERS = ['fabric', 'neoforge']  # Forge is only built for 1.20.1.
USER_AGENT = 'pauverblom/flight-affinity (weekly Minecraft version check)'


def fetch(url):
    request = urllib.request.Request(url, headers={'User-Agent': USER_AGENT})
    with urllib.request.urlopen(request, timeout=60) as response:
        return response.read()


def maven_versions(url):
    return re.findall(r'<version>([^<]+)</version>', fetch(url).decode())


def version_key(version):
    """Orders Minecraft and loader versions: 1.20 < 1.20.1 < 1.21 < 26.1 < 26.1.1; betas before releases."""
    numbers = [int(n) for n in re.findall(r'\d+', version.split('-')[0])]
    return numbers, 0 if '-' in version else 1


def read_props(path):
    props = {}
    for line in path.read_text().splitlines():
        line = line.strip()
        if line and not line.startswith('#') and '=' in line:
            key, value = line.split('=', 1)
            props[key.strip()] = value.strip()
    return props


def set_prop(path, key, value):
    text = path.read_text()
    if re.search(rf'(?m)^{re.escape(key)}=', text):
        text = re.sub(rf'(?m)^{re.escape(key)}=.*$', f'{key}={value}', text)
    else:
        text = text.rstrip('\n') + f'\n{key}={value}\n'
    path.write_text(text)


def targets():
    return {d.name: read_props(d / 'gradle.properties') for d in sorted(VERSIONS.iterdir()) if (d / 'gradle.properties').is_file()}


def release_targets(loader):
    """Release targets of a loader as (name, compile version, mc_targets)."""
    result = []
    for name, props in targets().items():
        parts = name.split('-')
        if len(parts) == 2 and parts[1] == loader and 'smoke_of' not in props:
            result.append((name, parts[0], props['mc_targets'].split()))
    return sorted(result, key=lambda t: version_key(t[1]))


def minecraft_releases():
    manifest = json.loads(fetch('https://piston-meta.mojang.com/mc/game/version_manifest_v2.json'))
    return sorted((v['id'] for v in manifest['versions'] if v['type'] == 'release'), key=version_key)


def neoforge_line(minecraft):
    """NeoForge version prefix for a Minecraft version: 1.21.4 -> 21.4, 1.21 -> 21.0, 26.1 -> 26.1.0, 26.1.2 -> 26.1.2."""
    parts = minecraft.split('.')
    if parts[0] == '1':
        return f'{parts[1]}.{parts[2] if len(parts) > 2 else 0}'
    return minecraft if len(parts) == 3 else minecraft + '.0'


def fabric_builds(minecraft):
    apis = [v for v in maven_versions('https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml')
            if v.endswith('+' + minecraft)]
    if not apis:
        return None, f'no Fabric API for {minecraft} yet'
    api = max(apis, key=version_key)
    jar = fetch(f'https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/{api}/fabric-api-{api}.jar')
    with zipfile.ZipFile(io.BytesIO(jar)) as z:
        required = json.loads(z.read('fabric.mod.json'))['depends'].get('fabricloader', '')
    loaders = json.loads(fetch('https://meta.fabricmc.net/v2/versions/loader'))
    latest = next(l['version'] for l in loaders if l.get('stable'))
    required_min = re.sub(r'^[^0-9]*', '', required) or latest
    return {'fabric_api': api, 'fabric_loader': latest, 'required_loader': required_min}, None


def neoforge_builds(minecraft):
    line = neoforge_line(minecraft)
    builds = sorted((v for v in maven_versions('https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml')
                     if v.startswith(line + '.') and re.fullmatch(rf'{re.escape(line)}\.\d+(-beta)?', v)), key=version_key)
    if not builds:
        return None, f'no NeoForge for {minecraft} yet'
    stable = [b for b in builds if not b.endswith('-beta')]
    return {'neoforge': builds[-1], 'neoforge_min': stable[0] if stable else builds[-1]}, None


def plan():
    """What is new: one entry per (Minecraft version, loader)."""
    releases = minecraft_releases()
    entries = []
    for loader in LOADERS:
        releases_of_loader = release_targets(loader)
        covered = {v for _, _, mc in releases_of_loader for v in mc}
        oldest = min(covered, key=version_key)
        for minecraft in releases:
            if version_key(minecraft) <= version_key(oldest) or minecraft in covered:
                continue
            base = [t for t in releases_of_loader if version_key(t[1]) < version_key(minecraft)][-1]
            entry = {'minecraft': minecraft, 'loader': loader, 'release_target': base[0],
                     'smoke_target': f'{minecraft}-{loader}'}
            try:
                builds, missing = (fabric_builds if loader == 'fabric' else neoforge_builds)(minecraft)
            except (urllib.error.URLError, KeyError, ValueError) as e:
                builds, missing = None, f'could not look up {loader} builds: {e}'
            entry['builds'] = builds
            entry['missing'] = missing
            entries.append(entry)
    return entries


def apply(entry):
    """Adds the Minecraft version to the release target and creates its smoke target."""
    minecraft, loader, base = entry['minecraft'], entry['loader'], entry['release_target']
    base_props_file = VERSIONS / base / 'gradle.properties'
    base_props = read_props(base_props_file)
    mc_targets = sorted(set(base_props['mc_targets'].split()) | {minecraft}, key=version_key)
    set_prop(base_props_file, 'mc_targets', ' '.join(mc_targets))
    builds = entry['builds']
    smoke = VERSIONS / entry['smoke_target']
    smoke.mkdir(exist_ok=True)
    lines = [f'# Smoke test: runs the GameTests on Minecraft {minecraft} with the jar built by {base}.',
             f'# Added by the weekly Minecraft version check.',
             f'smoke_of={base}', f'java_version={base_props["java_version"]}', '']
    if loader == 'fabric':
        lines += [f'fabric_loader={builds["fabric_loader"]}', f'fabric_api={builds["fabric_api"]}']
        # The jar must accept the Fabric Loader this Minecraft version's Fabric API needs.
        if version_key(builds['required_loader']) > version_key(base_props.get('fabric_loader_min', '0')):
            set_prop(base_props_file, 'fabric_loader_min', builds['required_loader'])
            # Development runs must still satisfy it.
            if version_key(base_props['fabric_loader']) < version_key(builds['required_loader']):
                set_prop(base_props_file, 'fabric_loader', builds['fabric_loader'])
    else:
        lines += [f'neoforge={builds["neoforge"]}', f'neoforge_min={builds["neoforge_min"]}']
    (smoke / 'gradle.properties').write_text('\n'.join(lines) + '\n')


def gradle(*tasks):
    result = subprocess.run(['./gradlew', '--continue', *tasks], cwd=ROOT, capture_output=True, text=True)
    output = result.stdout + result.stderr
    errors = [l for l in output.splitlines() if re.search(r'What went wrong|FAILED|> .*(failed|passed|not )', l)]
    return result.returncode == 0, '\n'.join(errors[-25:]) or output[-3000:]


def test(entry):
    smoke, base = entry['smoke_target'], entry['release_target']
    return gradle(f':{base}:build', f':{base}:checkReleaseJar', f':{smoke}:runGametest', f':{smoke}:productionTest')


def git(*args):
    return subprocess.run(['git', *args], cwd=ROOT, check=True, capture_output=True, text=True).stdout


def gh(*args):
    return subprocess.run(['gh', *args], cwd=ROOT, check=True, capture_output=True, text=True).stdout


def report(minecraft, entries, branch_pushed):
    repo = os.environ['GITHUB_REPOSITORY']
    default = json.loads(gh('repo', 'view', repo, '--json', 'defaultBranchRef'))['defaultBranchRef']['name']
    lines = [f'Minecraft **{minecraft}** is out and no jar covers it yet.', '']
    for e in entries:
        name = 'Fabric' if e['loader'] == 'fabric' else 'NeoForge'
        if e['missing']:
            lines.append(f'- **{name}**: waiting: {e["missing"]}. Checked again every week.')
        elif e['passed']:
            lines.append(f'- **{name}**: the `{e["release_target"]}` jar works on {minecraft}: GameTests and production '
                         f'tests pass with {minecraft} added to its `mc_targets` (smoke target `{e["smoke_target"]}`).')
        else:
            lines.append(f'- **{name}**: the `{e["release_target"]}` jar does **not** pass on {minecraft}; it needs a new '
                         f'target (code changes). Failures:\n\n```\n{e["log"]}\n```')
    if branch_pushed:
        lines += ['', f'The changes that passed are on [`{BRANCH}`](https://github.com/{repo}/tree/{BRANCH}). '
                      f'[Open a pull request](https://github.com/{repo}/compare/{default}...{BRANCH}?expand=1), '
                      'then run the release workflow after merging: only the jars with a new Minecraft range are uploaded.']
    lines += ['', 'Close this issue to stop the weekly updates for this version.']
    body = '\n'.join(lines)
    status = hashlib.sha256(json.dumps([[e['loader'], e['missing'], e.get('passed')] for e in entries]).encode()).hexdigest()[:12]
    body += f'\n\n<!-- status:{status} -->'
    title = f'Minecraft {minecraft}: add support'
    existing = json.loads(gh('issue', 'list', '--repo', repo, '--state', 'all', '--search', f'"{title}" in:title',
                             '--json', 'number,title,state,body'))
    issue = next((i for i in existing if i['title'] == title), None)
    owner = os.environ['GITHUB_REPOSITORY_OWNER']
    if issue is None:
        gh('issue', 'create', '--repo', repo, '--title', title, '--body', body, '--assignee', owner)
        return f'opened "{title}"'
    if issue['state'] != 'OPEN':
        return f'"{title}" is closed; not updated'
    if f'status:{status}' in issue['body']:
        return f'"{title}" unchanged'
    gh('issue', 'edit', str(issue['number']), '--repo', repo, '--body', body)
    gh('issue', 'comment', str(issue['number']), '--repo', repo, '--body', f'@{owner} update:\n\n' + body)
    return f'updated "{title}"'


def main():
    mode = sys.argv[1] if len(sys.argv) > 1 else 'plan'
    entries = plan()
    if not entries:
        print('Every Minecraft release is covered.')
        return
    for e in entries:
        print(f'{e["minecraft"]} {e["loader"]}: ' + (e['missing'] or f'builds {e["builds"]}, extend {e["release_target"]}'))
    if mode != 'check':
        return

    testable = [e for e in entries if not e['missing']]
    for e in testable:
        apply(e)
    for e in testable:
        print(f'Testing {e["smoke_target"]}...', flush=True)
        e['passed'], e['log'] = test(e)
        print('  passed' if e['passed'] else '  failed\n' + e['log'], flush=True)

    # Keep only the changes that passed.
    git('checkout', '--', 'versions')
    git('clean', '-fdq', 'versions')
    passed = [e for e in testable if e['passed']]
    for e in passed:
        apply(e)
    branch_pushed = False
    if passed:
        git('config', 'user.name', 'github-actions[bot]')
        git('config', 'user.email', '41898282+github-actions[bot]@users.noreply.github.com')
        git('checkout', '-B', BRANCH)
        git('add', 'versions')
        versions = ', '.join(sorted({e['minecraft'] for e in passed}, key=version_key))
        git('commit', '-m', f'Support Minecraft {versions} with the existing jars\n\nAdded by the weekly Minecraft version check.')
        git('push', '--force', 'origin', BRANCH)
        branch_pushed = True

    for minecraft in sorted({e['minecraft'] for e in entries}, key=version_key):
        of_version = [e for e in entries if e['minecraft'] == minecraft]
        print(report(minecraft, of_version, branch_pushed and any(e.get('passed') for e in of_version)))


if __name__ == '__main__':
    main()
