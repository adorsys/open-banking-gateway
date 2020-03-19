#!/usr/bin/env bash

echo "Validating DEV deployment"

if [[ $TRAVIS_REPO_SLUG != "adorsys/open-banking-gateway"
    || $TRAVIS_PULL_REQUEST != "false"
    || -z "$TRAVIS_COMMIT" ]];
then
  echo "ERROR: Deployment validation not allowed"
  exit 1
fi

COMMIT="$TRAVIS_COMMIT"
OPBA_URL=https://obg-dev-openbankinggateway.cloud.adorsys.de

# Wait for deployment to happen
timeout 600s bash -c 'while [[ $(curl -s "'"$OPBA_URL"'"/actuator/info | grep -c '"$COMMIT"') != 1 ]]; do sleep 10; done' || echo "Failed waiting for deploy $COMMIT"; exit 1

# Run smoke tests:
export ENABLE_SMOKE_TESTS=true
./mvnw test -DfailIfNoTests=false -Dtest=de.adorsys.opba.smoketests.OpbaApiSmokeE2ETest