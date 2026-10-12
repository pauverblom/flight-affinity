#!/usr/bin/env bash
# Fails unless the development GameTest run of a target passed every test. A target that registers no
# tests, or a server that crashes, would otherwise look like a pass. Usage: check-gametests.sh <target>
set -euo pipefail
# Every test has a test_instance file (needed from 1.21.5), so their count is the test count.
expected=$(ls src/test/resources/data/flightaffinity-test/test_instance | wc -l)
passed=$(grep -o -E 'All [0-9]+ required tests passed' "versions/$1/run/gametest/logs/latest.log" | grep -o -E '[0-9]+' | tail -1 || true)
echo "GameTests passed: ${passed:-0}, expected at least $expected"
[ "${passed:-0}" -ge "$expected" ]
