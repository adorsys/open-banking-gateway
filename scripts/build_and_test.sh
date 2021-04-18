#!/usr/bin/env bash

# log disk space to understand why heavy tests fail
while true; do df -h; sleep 10; done &
while true; do du -hs .; sleep 15; done &

echo "Building and testing JAVA backend"
bash ./scripts/build_mvn.sh || exit 1

echo "Building and testing NPM UI"
bash ./scripts/build_npm.sh || exit 1