#!/usr/bin/env bash

echo "Building and testing JAVA backend"
bash ./scripts/build_mvn.sh

echo "Building and testing NPM UI"
bash ./scripts/build_npm.sh