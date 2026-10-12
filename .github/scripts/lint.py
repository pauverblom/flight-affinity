#!/usr/bin/env python3
"""
Repository checks that need no build (CI runs them in the "checks" job; run locally the same way):
  - text files follow .editorconfig: UTF-8, LF line endings, a final newline, no trailing whitespace,
    spaces instead of tabs (except where a format needs tabs);
  - JSON resources are valid (templates with ${...} placeholders are checked after filling them in).
Usage: lint.py  (exit code 1 and one line per problem when something is wrong)
"""
import json
import re
import subprocess
import sys

TEXT = re.compile(r'\.(java|gradle|kts|properties|json|mcmeta|toml|ya?ml|md|py|sh|txt|cfg)$|(^|/)(LICENSE|\.gitignore|\.editorconfig|gradlew)$')
TABS_ALLOWED = re.compile(r'(^|/)gradlew(\.bat)?$')


def main():
    problems = []
    files = subprocess.run(['git', 'ls-files'], capture_output=True, text=True, check=True).stdout.split('\n')
    for path in filter(None, files):
        if not TEXT.search(path):
            continue
        data = open(path, 'rb').read()
        try:
            text = data.decode('utf-8')
        except UnicodeDecodeError:
            problems.append(f'{path}: not UTF-8')
            continue
        if '\r' in text:
            problems.append(f'{path}: CRLF line endings')
        if text and not text.endswith('\n'):
            problems.append(f'{path}: no newline at end of file')
        for number, line in enumerate(text.split('\n'), 1):
            if line != line.rstrip(' \t'):
                problems.append(f'{path}:{number}: trailing whitespace')
            if '\t' in line and not TABS_ALLOWED.search(path):
                problems.append(f'{path}:{number}: tab (indent with spaces)')
        if path.endswith(('.json', '.mcmeta')):
            # Templates are expanded by Gradle; check them with the placeholders filled in.
            filled = re.sub(r'<%.*?%>', '', text, flags=re.S)
            filled = re.sub(r'\$\{[^}]*\}', '1', filled)
            try:
                json.loads(filled)
            except json.JSONDecodeError as e:
                problems.append(f'{path}: invalid JSON: {e}')
    for problem in problems:
        print(problem)
    if problems:
        print(f'\n{len(problems)} problem(s). See .editorconfig.')
        sys.exit(1)
    print('Lint OK')


if __name__ == '__main__':
    main()
