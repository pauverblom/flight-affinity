#!/usr/bin/env bash
# Puts the lines that explain a failed CI job at the end of its log: Gradle errors and the relevant
# parts of the game logs. Usage: summarize-failure.sh <target>
dir=versions/$1/run
echo "::group::Gradle errors"
grep -n -A3 -E "error:|What went wrong|FAILED|Exception|Caused by" gradle.log | grep -v -E "^[0-9]+-\s+at |^[0-9]+:\s+at " | head -120 || true
echo "::endgroup::"
for log in "$dir/gametest/logs/latest.log" "$dir/production/logs/latest.log"; do
  [ -f "$log" ] || continue
  echo "::group::Errors in $log"
  grep -n -E "ERROR|FATAL|Exception|Caused by|failed|Failed|passed|tests" "$log" | grep -v -E "^[0-9]+:\s+at " | head -120 || true
  echo "::endgroup::"
  # Skips the GameTest progress bars, which can fill the log.
  echo "::group::Start of $log"
  grep -v -E "GameTestServer.*\]: \[[ +x_]*\]$" "$log" | head -n 150
  echo "::endgroup::"
  echo "::group::End of $log"
  grep -v -E "GameTestServer.*\]: \[[ +x_]*\]$" "$log" | tail -n 60
  echo "::endgroup::"
  echo "::group::First stack trace in $log"
  grep -v -E "server.properties|eula.txt" "$log" | grep -n -m1 -A60 -E "Exception|Error:" || true
  echo "::endgroup::"
done
debug=$dir/production/logs/debug.log
if [ -f "$debug" ]; then
  echo "::group::Command exceptions in $debug"
  grep -n -A25 "Command exception" "$debug" | head -80 || true
  echo "::endgroup::"
fi
