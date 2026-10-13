#!/usr/bin/env python3
"""
Download history for the README (.github/workflows/download-stats.yml).

Neither site publishes a history that combines both, so this records one row per day with each site's
total downloads into a CSV, and draws the history as an SVG chart. It also writes the data for the
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


def modrinth_history(token, total, days=3650):
    """Modrinth's total at the end of each past day: today's total minus what was downloaded since."""
    project_id = fetch_json(f'https://api.modrinth.com/v2/project/{MODRINTH}')['id']
    end = datetime.datetime.now(datetime.timezone.utc)
    start = end - datetime.timedelta(days=days)
    query = urllib.parse.urlencode({'project_ids': json.dumps([project_id]), 'start_date': start.isoformat(),
                                    'end_date': end.isoformat(), 'resolution_minutes': 1440})
    data = fetch_json(f'https://api.modrinth.com/v3/analytics/downloads?{query}', {'Authorization': token})
    per_day = {}
    for timestamp, count in data.get(project_id, {}).items():
        day = datetime.datetime.fromtimestamp(int(timestamp), datetime.timezone.utc).date().isoformat()
        per_day[day] = per_day.get(day, 0) + int(count)
    history, running = {}, total
    for day in sorted(per_day, reverse=True):
        history[day] = running
        running -= per_day[day]
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
    """Stacked areas (Modrinth below, CurseForge above), so the top edge is the combined total."""
    width, height, left, right, top, bottom = 720, 300, 56, 150, 44, 32
    days = [datetime.date.fromisoformat(d) for d in sorted(rows)]
    points = []
    for day in days:
        row = rows[day.isoformat()]
        m = int(row['modrinth']) if row.get('modrinth') else None
        c = int(row['curseforge']) if row.get('curseforge') else None
        points.append((day, m or 0, c))
    if len(points) == 1:
        # One day recorded: draw it as a flat band so the chart is not empty.
        points.insert(0, (days[0] - datetime.timedelta(days=1),) + points[0][1:])
    first, last = points[0][0], points[-1][0]
    span = max((last - first).days, 1)
    peak = max(m + (c or 0) for _, m, c in points) or 1
    # A round grid step (1, 2 or 5 times a power of ten) giving about four lines.
    magnitude = 10 ** max(len(str(peak // 4)) - 1, 0)
    step = next(f * magnitude for f in (1, 2, 5, 10) if f * magnitude * 4 >= peak)
    lines = -(-peak // step)
    ceiling = lines * step
    x = lambda d: left + (d - first).days / span * (width - left - right)
    y = lambda v: top + (1 - v / ceiling) * (height - top - bottom)

    def area(lower, upper):
        upper_pts = [f'{x(d):.1f},{y(u):.1f}' for d, u in upper]
        lower_pts = [f'{x(d):.1f},{y(l):.1f}' for d, l in reversed(lower)]
        return 'M' + ' L'.join(upper_pts + lower_pts) + ' Z'

    modrinth_upper = [(d, m) for d, m, _ in points]
    with_cf = [(d, m, c) for d, m, c in points if c is not None]
    parts = [f'<path class="modrinth" d="{area([(d, 0) for d, _, _ in points], modrinth_upper)}"/>']
    if with_cf:
        parts.append(f'<path class="curseforge" d="{area([(d, m) for d, m, _ in with_cf], [(d, m + c) for d, m, c in with_cf])}"/>')
        # 2px surface gap between the two areas.
        parts.append('<polyline class="gap" points="' + ' '.join(f'{x(d):.1f},{y(m):.1f}' for d, m, _ in with_cf) + '"/>')
    grid = []
    for i in range(lines + 1):
        value = step * i
        grid.append(f'<line class="grid" x1="{left}" x2="{width - right}" y1="{y(value):.1f}" y2="{y(value):.1f}"/>'
                    f'<text class="axis" x="{left - 8}" y="{y(value) + 4:.1f}" text-anchor="end">{number(value)}</text>')
    ticks = [first, first + (last - first) / 2, last] if span >= 2 else [last]
    labels = ''.join(f'<text class="axis" x="{x(d):.1f}" y="{height - 10}" text-anchor="middle">{d:%b %Y}</text>'
                     for d in ticks)
    _, m_now, c_now = points[-1]
    total = m_now + (c_now or 0)
    end_x = width - right + 10
    # Labels at the middle of each band's end, kept 16px apart and above the x axis labels.
    m_y = min(y(m_now / 2) + 4, height - bottom - 2)
    direct = [f'<text class="label" x="{end_x}" y="{m_y:.1f}">Modrinth {number(m_now)}</text>']
    if c_now is not None:
        c_y = min(y(m_now + c_now / 2) + 4, m_y - 16)
        direct.append(f'<text class="label" x="{end_x}" y="{c_y:.1f}">CurseForge {number(c_now)}</text>')
    legend = ('<rect class="modrinth" x="{0}" y="14" width="10" height="10" rx="2"/><text class="legend" x="{1}" y="23">Modrinth</text>'
              '<rect class="curseforge" x="{2}" y="14" width="10" height="10" rx="2"/><text class="legend" x="{3}" y="23">CurseForge</text>'
              ).format(left + 210, left + 224, left + 300, left + 314)
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}" height="{height}" role="img" aria-label="Flight Affinity downloads over time: {number(total)} in total, Modrinth {number(m_now)}, CurseForge {number(c_now or 0)}">
<style>
  svg {{ font: 12px -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif; }}
  .bg {{ fill: #fcfcfb; }} .title {{ fill: #0b0b0b; font-weight: 600; font-size: 14px; }}
  .axis, .legend {{ fill: #52514e; }} .label {{ fill: #0b0b0b; }} .grid {{ stroke: #e4e3df; stroke-width: 1; }}
  .modrinth {{ fill: #2a78d6; }} .curseforge {{ fill: #eb6834; }} .gap {{ fill: none; stroke: #fcfcfb; stroke-width: 2; }}
  @media (prefers-color-scheme: dark) {{
    .bg {{ fill: #1a1a19; }} .title, .label {{ fill: #f0efec; }} .axis, .legend {{ fill: #c3c2b7; }}
    .grid {{ stroke: #33332f; }} .modrinth {{ fill: #3987e5; }} .curseforge {{ fill: #d95926; }} .gap {{ stroke: #1a1a19; }}
  }}
</style>
<rect class="bg" width="{width}" height="{height}" rx="8"/>
<text class="title" x="{left}" y="24">Downloads: {number(total)}</text>
{legend}
{''.join(grid)}
{''.join(parts)}
{''.join(direct)}
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
            for day, total in modrinth_history(token, m).items():
                rows.setdefault(day, {'date': day})['modrinth'] = str(total)
        except Exception as e:
            print(f'::warning::Modrinth history unavailable ({e}); does MODRINTH_TOKEN have the analytics scope?')
    rows[today] = {'date': today, 'modrinth': str(m), 'curseforge': '' if c is None else str(c)}
    write_rows(data, rows)
    (out / 'downloads.svg').write_text(chart(read_rows(data)))
    (out / 'minecraft.json').write_text(json.dumps(
        {'schemaVersion': 1, 'label': 'Minecraft', 'message': minecraft_range(), 'color': '62b47a'}) + '\n')
    print(f'{today}: Modrinth {m}, CurseForge {c}; {len(rows)} days recorded')


if __name__ == '__main__':
    main()
