#!/usr/bin/env bash

if [[ "$MVN_TESTS_DISABLED" == "true" ]]; then
  echo "Maven tests are disabled, only building"
  mvn verify --no-transfer-progress -Djgiven.report.text=false -DskipTests || exit 1
else
  echo "Building and testing"
  mvn verify --no-transfer-progress -Djgiven.report.text=false || exit 1
fi
