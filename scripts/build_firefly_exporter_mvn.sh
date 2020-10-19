#!/usr/bin/env bash

# Build FireFly-Exporter separately, as it is not fulltime-deployment part
echo "Building FireFly-exporter"
cd firefly-exporter
mvn verify --no-transfer-progress
