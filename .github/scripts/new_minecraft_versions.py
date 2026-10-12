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
       new_minecraft_versions.py try     (adds and tests like check, prints the issue text instead)
       new_minecraft_versions.py plan    (only prints what is new; no changes, no tests)
Option --forget <minecraft version>: first remove that version from the working tree (its release
targets, smoke targets and mc_targets entries), to rehearse its release. Used by the workflow when
the check itself changes.
"""
import base64
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
# The GitHub token is only handed to gh and to the final git push, not to Gradle and the game servers
# it starts (they run freshly published loader builds).
TOKEN = os.environ.pop('GH_TOKEN', None)


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


def version_manifest():
    if not hasattr(version_manifest, 'cached'):
        version_manifest.cached = json.loads(fetch('https://piston-meta.mojang.com/mc/game/version_manifest_v2.json'))
    return version_manifest.cached


def minecraft_releases():
    return sorted((v['id'] for v in version_manifest()['versions'] if v['type'] == 'release'), key=version_key)


def java_version(minecraft):
    """The Java version a Minecraft version runs on (the smoke target's; the jar may target an older one)."""
    url = next(v['url'] for v in version_manifest()['versions'] if v['id'] == minecraft)
    return json.loads(fetch(url))['javaVersion']['majorVersion']


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
    # Usually ">=0.17.2"; a list of alternatives is also allowed by fabric.mod.json.
    if isinstance(required, list):
        required = required[0] if required else ''
    found = re.search(r'\d+(\.\d+)+', required)
    required_min = found.group(0) if found else latest
    return {'fabric_api': api, 'fabric_loader': latest, 'required_loader': required_min}, None


def quilt_builds(minecraft):
    games = {v['version'] for v in json.loads(fetch('https://meta.quiltmc.org/v3/versions/game'))}
    if minecraft not in games:
        return None, f'Quilt does not support {minecraft} yet'
    loaders = [v for v in maven_versions('https://maven.quiltmc.org/repository/release/org/quiltmc/quilt-loader/maven-metadata.xml')
               if re.fullmatch(r'[\d.]+', v)]
    return {'quilt_loader': max(loaders, key=version_key)}, None


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
            before = [t for t in releases_of_loader if version_key(t[1]) < version_key(minecraft)]
            if not before:
                continue  # Older than every jar's compile version: not something a new release does.
            base = before[-1]
            entry = {'minecraft': minecraft, 'loader': loader, 'release_target': base[0],
                     'smoke_target': f'{minecraft}-{loader}'}
            # Loaders the jar is also published for need the version too (Quilt for Fabric jars).
            also = read_props(VERSIONS / base[0] / 'gradle.properties').get('also_runs_on', '').split()
            entry['also'] = also
            try:
                builds, missing = (fabric_builds if loader == 'fabric' else neoforge_builds)(minecraft)
                if builds:
                    builds['java_version'] = java_version(minecraft)
                if builds and 'quilt' in also:
                    quilt, missing = quilt_builds(minecraft)
                    builds = {**builds, **quilt} if quilt else None
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
             f'smoke_of={base}', f'java_version={builds["java_version"]}', '']
    if loader == 'fabric':
        lines += [f'fabric_loader={builds["fabric_loader"]}']
        # The production test runs the jar's minimum Fabric Loader, unless this Minecraft version's
        # Fabric API needs a newer one. The jar keeps its minimum: older Minecraft versions run on it.
        if version_key(builds['required_loader']) > version_key(base_props['fabric_loader_min']):
            lines += ["# Oldest Fabric Loader this Minecraft version's Fabric API accepts (newer than the jar's own",
                      '# minimum); the production tests run on it.', f'fabric_loader_min={builds["required_loader"]}']
        lines += [f'fabric_api={builds["fabric_api"]}']
    else:
        lines += [f'neoforge={builds["neoforge"]}', f'neoforge_min={builds["neoforge_min"]}']
    (smoke / 'gradle.properties').write_text('\n'.join(lines) + '\n')
    if 'quilt' in entry['also']:
        quilt = VERSIONS / f'{minecraft}-{loader}-quilt'
        quilt.mkdir(exist_ok=True)
        (quilt / 'gradle.properties').write_text('\n'.join([
            f'# Smoke test: the {base} jar on a Quilt server with Minecraft {minecraft} (Quilt runs Fabric mods).',
            f'# Added by the weekly Minecraft version check.',
            f'smoke_of={base}', f'java_version={builds["java_version"]}', 'production_server=true', '',
            f'quilt_loader={builds["quilt_loader"]}', f'fabric_api={builds["fabric_api"]}']) + '\n')


NETWORK_ERROR = re.compile(r'status code (5\d\d|429)|response code: (5\d\d|429)|answered HTTP (5\d\d|429)|'
                           r'Read timed out|Connect(ion)? timed out|Connection reset|Remote host terminated the handshake')


def gradle(*tasks):
    """Runs Gradle (retrying network failures, see gradle.sh); returns (passed, log excerpt, network failure)."""
    result = subprocess.run([str(ROOT / '.github/scripts/gradle.sh'), '--continue', *tasks], cwd=ROOT,
                            capture_output=True, text=True)
    output = result.stdout + result.stderr
    errors = [l for l in output.splitlines() if re.search(r'What went wrong|FAILED|> .*(failed|passed|not )', l)]
    passed = result.returncode == 0
    return passed, '\n'.join(errors[-25:]) or output[-3000:], not passed and bool(NETWORK_ERROR.search(output))


def test(entry):
    smoke, base = entry['smoke_target'], entry['release_target']
    others = [f':{smoke}-{other}:productionTest' for other in entry['also']]
    return gradle(f':{base}:build', f':{base}:checkReleaseJar', f':{smoke}:runGametest', f':{smoke}:productionTest', *others)


def git(*args):
    return subprocess.run(['git', *args], cwd=ROOT, check=True, capture_output=True, text=True).stdout


def gh(*args):
    return subprocess.run(['gh', *args], cwd=ROOT, check=True, capture_output=True, text=True,
                          env={**os.environ, 'GH_TOKEN': TOKEN or ''}).stdout


def push_branch():
    """Pushes BRANCH unless it already has this content; returns whether it was pushed."""
    exists = subprocess.run(['git', 'ls-remote', '--exit-code', '--heads', 'origin', BRANCH], cwd=ROOT,
                            capture_output=True).returncode == 0
    if exists:
        git('fetch', '-q', 'origin', BRANCH)
        if git('rev-parse', f'origin/{BRANCH}^{{tree}}') == git('rev-parse', 'HEAD^{tree}'):
            return False
    # The workflow checks out without persisted credentials (see TOKEN).
    auth = base64.b64encode(f'x-access-token:{TOKEN}'.encode()).decode()
    git('-c', f'http.https://github.com/.extraheader=AUTHORIZATION: basic {auth}', 'push', '--force', 'origin', BRANCH)
    return True


def forget(minecraft):
    for name, props in targets().items():
        if name.split('-')[0] == minecraft and 'smoke_of' not in props:
            sys.exit(f'{name} builds a jar; only versions that are smoke targets can be forgotten')
    for name, props in targets().items():
        directory = VERSIONS / name
        if name.split('-')[0] == minecraft:
            subprocess.run(['rm', '-r', str(directory)], check=True)
        elif 'mc_targets' in props and minecraft in props['mc_targets'].split():
            set_prop(directory / 'gradle.properties', 'mc_targets', ' '.join(v for v in props['mc_targets'].split() if v != minecraft))


def issue_body(minecraft, entries, repo, default, branch_pushed):
    lines = [f'Minecraft **{minecraft}** is out and no jar covers it yet.', '']
    for e in entries:
        name = 'Fabric' if e['loader'] == 'fabric' else 'NeoForge'
        if e['missing']:
            lines.append(f'- **{name}**: waiting: {e["missing"]}. Checked again every week.')
        elif e['passed']:
            also = ''.join(f' and on {other.capitalize()}' for other in e['also'])
            lines.append(f'- **{name}**: the `{e["release_target"]}` jar works on {minecraft}{also}: GameTests and '
                         f'production tests pass with {minecraft} added to its `mc_targets` (smoke target `{e["smoke_target"]}`).')
        else:
            lines.append(f'- **{name}**: the `{e["release_target"]}` jar does **not** pass on {minecraft}; it needs a new '
                         f'target (code changes). Failures:\n\n```\n{e["log"]}\n```')
    if branch_pushed:
        lines += ['', f'The changes that passed are on [`{BRANCH}`](https://github.com/{repo}/tree/{BRANCH}). '
                      f'[Open a pull request](https://github.com/{repo}/compare/{default}...{BRANCH}?expand=1), '
                      'then run the release workflow after merging: only the jars with a new Minecraft range are uploaded.']
    lines += ['', 'Close this issue to stop the weekly updates for this version.']
    return '\n'.join(lines)


def report(minecraft, entries, branch_pushed):
    repo = os.environ['GITHUB_REPOSITORY']
    default = json.loads(gh('repo', 'view', repo, '--json', 'defaultBranchRef'))['defaultBranchRef']['name']
    body = issue_body(minecraft, entries, repo, default, branch_pushed)
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
    args = sys.argv[1:]
    mode = args[0] if args else 'plan'
    forgotten = args[args.index('--forget') + 1] if '--forget' in args else None
    if forgotten:
        forget(forgotten)
    entries = plan()
    if not entries:
        print('Every Minecraft release is covered.')
        return
    for e in entries:
        print(f'{e["minecraft"]} {e["loader"]}: ' + (e['missing'] or f'builds {e["builds"]}, extend {e["release_target"]}'))
    if mode not in ('check', 'try'):
        return

    testable = [e for e in entries if not e['missing']]
    for e in testable:
        apply(e)
    for e in testable:
        print(f'Testing {e["smoke_target"]}...', flush=True)
        e['passed'], e['log'], network = test(e)
        if network:
            # Not the jar's fault: report it as waiting and try again next week.
            e['missing'] = 'the test could not download what it needs (network errors); trying again next week'
        print('  passed' if e['passed'] else '  failed\n' + e['log'], flush=True)

    if mode == 'try':
        for minecraft in sorted({e['minecraft'] for e in entries}, key=version_key):
            of_version = [e for e in entries if e['minecraft'] == minecraft]
            print(f'\n--- Issue "Minecraft {minecraft}: add support" would say:\n')
            print(issue_body(minecraft, of_version, os.environ.get('GITHUB_REPOSITORY', 'owner/repo'), 'main',
                             any(e.get('passed') for e in of_version)))
        # A rehearsal must have tested the forgotten version on every loader, not skipped it.
        rehearsed = [e for e in entries if e['minecraft'] == forgotten]
        if forgotten and len([e for e in rehearsed if e.get('passed')]) != len(LOADERS):
            sys.exit(f'Rehearsal of {forgotten}: not added and tested on every loader')
        sys.exit(0 if all(e.get('passed') for e in testable) else 1)

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
        if push_branch():
            # Pushes with the workflow's token start no workflows; start CI on the branch explicitly.
            gh('workflow', 'run', 'build.yml', '--ref', BRANCH)
        branch_pushed = True

    for minecraft in sorted({e['minecraft'] for e in entries}, key=version_key):
        of_version = [e for e in entries if e['minecraft'] == minecraft]
        print(report(minecraft, of_version, branch_pushed and any(e.get('passed') for e in of_version)))


if __name__ == '__main__':
    main()
