#!/usr/bin/env python3
"""
Download history for the README (.github/workflows/download-stats.yml).

Neither site publishes a history that combines both, so this records one row per day with each site's
total downloads into a CSV, and draws the history as an SVG chart (one line per site). It also writes the data for the
README's Minecraft version badge (shields.io endpoint format). The workflow keeps the files on the
`stats` branch.

Usage: download_stats.py <output dir> [--backfill]
  --backfill  also fills in Modrinth's past days from its analytics (needs MODRINTH_TOKEN with the
              analytics scope). CurseForge has no history API; its line starts on the first recorded day.
"""
import csv
import datetime
import json
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MODRINTH = 'flight-affinity'
CURSEFORGE = '1180063'
USER_AGENT = 'pauverblom/flight-affinity (download statistics)'


def fetch_json(url, headers=None, attempts=4):
    for attempt in range(1, attempts + 1):
        request = urllib.request.Request(url, headers={'User-Agent': USER_AGENT, **(headers or {})})
        try:
            with urllib.request.urlopen(request, timeout=60) as response:
                # cfwidget answers 202 while it fetches a project it has not seen recently.
                if response.status == 202 and attempt < attempts:
                    time.sleep(30)
                    continue
                return json.loads(response.read())
        except (urllib.error.URLError, TimeoutError) as e:
            if attempt == attempts or (isinstance(e, urllib.error.HTTPError) and e.code < 500 and e.code != 429):
                raise
        time.sleep(15 * attempt)
    raise RuntimeError(f'{url}: no answer after {attempts} attempts')


def modrinth_total():
    return int(fetch_json(f'https://api.modrinth.com/v2/project/{MODRINTH}')['downloads'])


def curseforge_total():
    return int(fetch_json(f'https://api.cfwidget.com/{CURSEFORGE}')['downloads']['total'])


def post_json(url, body, headers):
    request = urllib.request.Request(url, data=json.dumps(body).encode(), method='POST',
                                     headers={'User-Agent': USER_AGENT, 'Content-Type': 'application/json', **headers})
    with urllib.request.urlopen(request, timeout=120) as response:
        return json.loads(response.read())


def modrinth_history(token, total):
    """
    Modrinth's total at the end of each past day: today's total minus what was downloaded since.
    Uses Modrinth's analytics (POST /v3/analytics, one slice per day, at most 1024 slices a request).
    """
    project = fetch_json(f'https://api.modrinth.com/v2/project/{MODRINTH}')
    day = datetime.timedelta(days=1)
    today = datetime.datetime.now(datetime.timezone.utc).replace(hour=0, minute=0, second=0, microsecond=0)
    start = datetime.datetime.fromisoformat(project['published'].replace('Z', '+00:00')).replace(
        hour=0, minute=0, second=0, microsecond=0)
    per_day = {}
    while start < today + day:
        end = min(start + 1000 * day, today + day)
        data = post_json('https://api.modrinth.com/v3/analytics', {
            'time_range': {'start': start.isoformat(), 'end': end.isoformat(), 'resolution': {'minutes': 1440}},
            'return_metrics': {'project_downloads': {'bucket_by': []}},
            'project_ids': [project['id']],
        }, {'Authorization': token})
        for i, time_slice in enumerate(data['metrics']):
            downloads = sum(int(e.get('downloads', 0)) for e in time_slice if e.get('metric_kind') == 'downloads')
            per_day[(start + i * day).date().isoformat()] = downloads
        start = end
    history, running = {}, total
    for date in sorted(per_day, reverse=True):
        history[date] = running
        running -= per_day[date]
    return history


def minecraft_range():
    versions = []
    for props in (ROOT / 'versions').glob('*/gradle.properties'):
        found = re.search(r'(?m)^mc_targets=(.*)$', props.read_text())
        if found:
            versions += found.group(1).split()
    key = lambda v: [int(n) for n in v.split('.')]
    return f'{min(versions, key=key)} – {max(versions, key=key)}'


def read_rows(path):
    if not path.exists():
        return {}
    with path.open() as f:
        return {row['date']: row for row in csv.DictReader(f)}


def write_rows(path, rows):
    with path.open('w', newline='') as f:
        writer = csv.DictWriter(f, ['date', 'modrinth', 'curseforge'], lineterminator='\n')
        writer.writeheader()
        for day in sorted(rows):
            writer.writerow({k: rows[day].get(k, '') for k in ['date', 'modrinth', 'curseforge']})


def number(n):
    return f'{n / 1000:.1f}k'.replace('.0k', 'k') if n >= 1000 else str(n)


def chart(rows):
    """One line per site (CurseForge has no history before its first recorded day, so no stacking)."""
    width, height, left, right, top, bottom = 720, 300, 56, 150, 44, 32
    series = {'modrinth': [], 'curseforge': []}
    for date in sorted(rows):
        for name in series:
            if rows[date].get(name):
                series[name].append((datetime.date.fromisoformat(date), int(rows[date][name])))
    days = sorted({d for points in series.values() for d, _ in points})
    first, last = days[0], days[-1]
    span = max((last - first).days, 1)
    peak = max(v for points in series.values() for _, v in points) or 1
    # A round grid step (1, 2 or 5 times a power of ten) giving about four lines.
    magnitude = 10 ** max(len(str(peak // 4)) - 1, 0)
    step = next(f * magnitude for f in (1, 2, 5, 10) if f * magnitude * 4 >= peak)
    lines = -(-peak // step)
    ceiling = lines * step
    x = lambda d: left + (d - first).days / span * (width - left - right) if len(days) > 1 else (left + width - right) / 2
    y = lambda v: top + (1 - v / ceiling) * (height - top - bottom)

    grid = []
    for i in range(lines + 1):
        value = step * i
        grid.append(f'<line class="grid" x1="{left}" x2="{width - right}" y1="{y(value):.1f}" y2="{y(value):.1f}"/>'
                    f'<text class="axis" x="{left - 8}" y="{y(value) + 4:.1f}" text-anchor="end">{number(value)}</text>')
    ticks = [first, first + (last - first) / 2, last] if span >= 60 else [first, last] if span >= 2 else [last]
    label_format = '%b %Y' if span >= 60 else '%d %b'
    labels = ''.join(f'<text class="axis" x="{x(d):.1f}" y="{height - 10}" text-anchor="middle">{d.strftime(label_format)}</text>'
                     for d in ticks)
    marks, ends = [], []
    for name, points in series.items():
        if not points:
            continue
        if len(points) > 1:
            marks.append(f'<polyline class="line {name}" points="' + ' '.join(f'{x(d):.1f},{y(v):.1f}' for d, v in points) + '"/>')
        d, v = points[-1]
        marks.append(f'<circle class="dot {name}" cx="{x(d):.1f}" cy="{y(v):.1f}" r="4"/>')
        ends.append([y(v) + 4, name, v])
    # Direct labels at each line's end, kept 16px apart.
    ends.sort()
    for i in range(1, len(ends)):
        ends[i][0] = max(ends[i][0], ends[i - 1][0] + 16)
    names = {'modrinth': 'Modrinth', 'curseforge': 'CurseForge'}
    direct = ''.join(f'<text class="label" x="{width - right + 10}" y="{ly:.1f}">{names[n]} {number(v)}</text>' for ly, n, v in ends)
    total = sum(points[-1][1] for points in series.values() if points)
    legend = ('<rect class="key modrinth" x="{0}" y="14" width="10" height="10" rx="2"/><text class="legend" x="{1}" y="23">Modrinth</text>'
              '<rect class="key curseforge" x="{2}" y="14" width="10" height="10" rx="2"/><text class="legend" x="{3}" y="23">CurseForge</text>'
              ).format(left + 210, left + 224, left + 300, left + 314)
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}" height="{height}" role="img" aria-label="Flight Affinity downloads over time: {number(total)} in total">
<style>
  svg {{ font: 12px -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif; }}
  .bg {{ fill: #fcfcfb; }} .title {{ fill: #0b0b0b; font-weight: 600; font-size: 14px; }}
  .axis, .legend {{ fill: #52514e; }} .label {{ fill: #0b0b0b; }} .grid {{ stroke: #e4e3df; stroke-width: 1; }}
  .line {{ fill: none; stroke-width: 2; stroke-linejoin: round; stroke-linecap: round; }}
  .dot {{ stroke: #fcfcfb; stroke-width: 2; }}
  .line.modrinth {{ stroke: #2a78d6; }} .line.curseforge {{ stroke: #eb6834; }}
  .key.modrinth, .dot.modrinth {{ fill: #2a78d6; }} .key.curseforge, .dot.curseforge {{ fill: #eb6834; }}
  @media (prefers-color-scheme: dark) {{
    .bg {{ fill: #1a1a19; }} .title, .label {{ fill: #f0efec; }} .axis, .legend {{ fill: #c3c2b7; }}
    .grid {{ stroke: #33332f; }} .dot {{ stroke: #1a1a19; }}
    .line.modrinth {{ stroke: #3987e5; }} .line.curseforge {{ stroke: #d95926; }}
    .key.modrinth, .dot.modrinth {{ fill: #3987e5; }} .key.curseforge, .dot.curseforge {{ fill: #d95926; }}
  }}
</style>
<rect class="bg" width="{width}" height="{height}" rx="8"/>
<text class="title" x="{left}" y="24">Downloads: {number(total)}</text>
{legend}
{''.join(grid)}
{''.join(marks)}
{direct}
{labels}
</svg>
'''


def main():
    out = Path(sys.argv[1])
    out.mkdir(parents=True, exist_ok=True)
    data = out / 'downloads.csv'
    rows = read_rows(data)
    today = datetime.datetime.now(datetime.timezone.utc).date().isoformat()
    m = modrinth_total()
    try:
        c = curseforge_total()
    except Exception as e:  # cfwidget is a third-party service; a missing day is fine.
        print(f'::warning::CurseForge total unavailable: {e}')
        c = None
    if '--backfill' in sys.argv:
        token = os.environ.get('MODRINTH_TOKEN')
        try:
            history = modrinth_history(token, m)
            print(f'Modrinth history: {len(history)} days')
            for day, total in history.items():
                rows.setdefault(day, {'date': day})['modrinth'] = str(total)
        except Exception as e:
            detail = e.read().decode(errors='replace')[:500] if isinstance(e, urllib.error.HTTPError) else ''
            print(f'::warning::Modrinth history unavailable ({e} {detail}); does MODRINTH_TOKEN have the analytics scope?')
    rows[today] = {'date': today, 'modrinth': str(m), 'curseforge': '' if c is None else str(c)}
    write_rows(data, rows)
    (out / 'downloads.svg').write_text(chart(read_rows(data)))
    (out / 'minecraft.json').write_text(json.dumps(
        {'schemaVersion': 1, 'label': 'Minecraft', 'message': minecraft_range(), 'color': '62b47a'}) + '\n')
    print(f'{today}: Modrinth {m}, CurseForge {c}; {len(rows)} days recorded')


if __name__ == '__main__':
    main()
