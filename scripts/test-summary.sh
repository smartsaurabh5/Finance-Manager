#!/usr/bin/env bash
set -o pipefail

BASE_URL="${1:-http://localhost:8080/api}"
REQUIRED_RATE="${2:-80}"

printf '\n\033[1;32mTEST EXECUTION SUMMARY\033[0m\n\n'

./mvnw test
MAVEN_EXIT=$?

REPORT_DIR="target/surefire-reports"

if [ ! -d "$REPORT_DIR" ]; then
  printf '\n\033[1;31mNo test report directory found: %s\033[0m\n' "$REPORT_DIR"
  exit "$MAVEN_EXIT"
fi

SUMMARY=$(
  awk '
    BEGIN { tests=0; failures=0; errors=0; skipped=0 }
    /<testsuite / {
      for (i = 1; i <= NF; i++) {
        if ($i ~ /^tests=/) {
          gsub(/tests=|"|>/, "", $i); tests += $i
        }
        if ($i ~ /^failures=/) {
          gsub(/failures=|"|>/, "", $i); failures += $i
        }
        if ($i ~ /^errors=/) {
          gsub(/errors=|"|>/, "", $i); errors += $i
        }
        if ($i ~ /^skipped=/) {
          gsub(/skipped=|"|>/, "", $i); skipped += $i
        }
      }
    }
    END {
      failed = failures + errors
      passed = tests - failed - skipped
      rate = tests == 0 ? 0 : int((passed / tests) * 100)
      printf "%d %d %d %d %d", tests, passed, failed, skipped, rate
    }
  ' "$REPORT_DIR"/TEST-*.xml
)

read -r TOTAL PASSED FAILED SKIPPED RATE <<< "$SUMMARY"

printf '\n\033[1;32m====================================================\033[0m\n'
printf '\033[1;32mBase URL: %s\033[0m\n' "$BASE_URL"
printf '\033[1;32mTotal Tests Executed: %s\033[0m\n' "$TOTAL"
printf '\033[1;32mTests Passed: %s\033[0m\n' "$PASSED"
printf '\033[1;32mTests Failed: %s\033[0m\n' "$FAILED"
printf '\033[1;32mTests Skipped: %s\033[0m\n' "$SKIPPED"
printf '\033[1;32mSuccess Rate: %s%%\033[0m\n\n' "$RATE"
printf '\033[1;32mRequired Success Rate: %s%%\033[0m\n\n' "$REQUIRED_RATE"

if [ "$RATE" -ge "$REQUIRED_RATE" ] && [ "$MAVEN_EXIT" -eq 0 ]; then
  printf '\033[1;32mALL TESTS PASSED!\033[0m\n'
  printf '\033[1;32mThe Personal Finance Manager API test suite is working correctly.\033[0m\n'
else
  printf '\033[1;31mTEST THRESHOLD NOT MET. Check Maven output above.\033[0m\n'
fi

exit "$MAVEN_EXIT"
