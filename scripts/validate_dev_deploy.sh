#!/usr/bin/env bash

echo "Validating DEV deployment"

if [[ "$DISABLE_SMOKE_TESTS"  == "true" ]]; then
  echo "Smoke tests are swithced off by DISABLE_SMOKE_TESTS variable"
  exit 0
fi

if [[ $GITHUB_REPOSITORY != "adorsys/open-banking-gateway"
    || $GITHUB_EVENT_NAME == "pull_request"
    || -z "$TRAVIS_COMMIT" ]];
then
  echo "ERROR: Deployment validation not allowed"
  exit 1
fi

COMMIT="$GITHUB_SHA"
OPBA_URL=https://obg-dev-openbankinggateway.cloud.adorsys.de

function fail() {
    echo "Failed waiting for $COMMIT to be deployed";
    exit 1;
}

echo "Waiting for deployment"
# Wait for deployment to happen
timeout 600s bash -c 'while [[ $(wget -qO- '$OPBA_URL'/actuator/info | grep -c '$COMMIT') != 1 ]]; do echo "Wait for actuator to return desired commit '$COMMIT' / current: "$(wget -qO- '$OPBA_URL'/actuator/info)""; sleep 10; done' || fail

echo "$COMMIT is deployed"
echo "Run smoke tests"
# Run smoke tests:
export ENABLE_SMOKE_TESTS=true
echo "Run API smoke tests"
./mvnw test --no-transfer-progress -Djgiven.report.text=false -DfailIfNoTests=false -Dtest=de.adorsys.opba.smoketests.OpbaApiSmokeE2ETest || exit 1
echo "Run Consent UI and API smoke tests"
./mvnw test --no-transfer-progress -Djgiven.report.text=false -DfailIfNoTests=false -Dtest=de.adorsys.opba.smoketests.OpbaApiWithConsentUiSmokeE2ETest || exit 1
