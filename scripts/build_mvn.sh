#!/usr/bin/env bash

# Run heavy tests daily on cron.
# In particular it is sandbox-spinning tests that are launched if ENABLE_HEAVY_TESTS==true
if [[ "$TRAVIS_EVENT_TYPE" == "cron"* ]]; then
    export ENABLE_HEAVY_TESTS="true";
fi

mvn verify cobertura:cobertura