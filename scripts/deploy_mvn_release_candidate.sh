#!/usr/bin/env bash

set -e

# Note - these should be NOT production secrets
echo "$GPG_SECRET_KEY" | base64 --decode | $GPG_EXECUTABLE --import || true
echo "$GPG_OWNERTRUST" | base64 --decode | $GPG_EXECUTABLE --import-ownertrust || true
mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml package gpg:sign deploy -Prelease -DskipTests -Dregistry="$DEVELOP_MAVEN_REPOSITORY" -Dtoken="$DEPLOY_TOKEN" -B -U;