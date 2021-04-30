#!/usr/bin/env bash

echo "Building and testing JAVA backend"
bash ./scripts/build_mvn.sh || exit 1

echo "Building and testing NPM UI"
bash ./scripts/build_npm.sh || exit 1