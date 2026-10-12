#!/usr/bin/env bash
# Runs ./gradlew with the given arguments, appending its output to gradle.log. When it fails because of
# the network (a repository answering 5xx or 429, timeouts, resets), it is retried up to twice, after
# a pause; any other failure (a test, a compile error) fails at once.
set -uo pipefail
network_error='status code (5[0-9][0-9]|429)|response code: (5[0-9][0-9]|429)|Read timed out|Connect(ion)? timed out|Connection reset|Remote host terminated the handshake|Could not GET .* Bad Gateway'
for attempt in 1 2 3; do
  ./gradlew "$@" 2>&1 | tee attempt.log
  status=${PIPESTATUS[0]}
  cat attempt.log >> gradle.log
  if [ "$status" -eq 0 ]; then exit 0; fi
  if [ "$attempt" -eq 3 ] || ! grep -q -E "$network_error" attempt.log; then exit "$status"; fi
  echo "::warning::Network error, retrying in $((attempt * 60)) s (attempt $attempt of 3)"
  sleep $((attempt * 60))
done
