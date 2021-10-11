#!/usr/bin/env bash

# Build FireFly-Exporter separately, as it is not fulltime-deployment part
echo "Building FireFly-exporter"
cd firefly-exporter
../mvnw verify --no-transfer-progress

if [[ "$MVN_TESTS_DISABLED" == "true" ]]; then
  echo "Maven tests are disabled, only building FireFly exporter"
  ../mvnw verify --no-transfer-progress -DskipTests
else
  echo "Building and testing FireFly exporter"
  ../mvnw verify --no-transfer-progress
fi