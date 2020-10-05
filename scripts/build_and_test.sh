#!/usr/bin/env bash

echo "Building and testing JAVA backend"
bash ./scripts/build_mvn.sh || exit 1