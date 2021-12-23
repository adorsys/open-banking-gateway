#!/usr/bin/env bash

if [[ "$MVN_TESTS_DISABLED" == "true" ]]; then
  echo "Maven tests are disabled, only building"
  ./mvnw verify --no-transfer-progress -Djgiven.report.text=false -DskipTests || exit 1
else
  echo "Building and testing"
  ./mvnw verify --no-transfer-progress -Djgiven.report.text=false -DsurefireArgLine="-Xmx1024m -XX:MaxPermSize=256m" || exit 1
fi
