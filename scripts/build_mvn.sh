#!/usr/bin/env bash

# Run heavy tests daily on cron.
# In particular it is sandbox-spinning tests that are launched if ENABLE_HEAVY_TESTS==true
if [[ "$TRAVIS_EVENT_TYPE" == "cron"* || "$TRAVIS_BRANCH" == *"heavy-tests-enabled"* || "$TRAVIS_PULL_REQUEST_BRANCH" == *"heavy-tests-enabled"* ]]; then
    echo "Heavy tests enabled"
    export ENABLE_HEAVY_TESTS="true";
fi

mvn verify --no-transfer-progress