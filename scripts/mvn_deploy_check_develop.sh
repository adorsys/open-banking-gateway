#!/usr/bin/env bash

set -e

# Note - these should be NOT production secrets
echo "$GPG_SECRET_KEY" | base64 --decode | $GPG_EXECUTABLE --import --no-tty --batch --yes || true
echo "$GPG_OWNERTRUST" | base64 --decode | $GPG_EXECUTABLE --import-ownertrust --no-tty --batch --yes || true

# Deploy is actually skipped because artifact bundle is quite heavy for day-to-day CI/CD. But we sign them and run JavaDocs
mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml package gpg:sign -Dmaster-branch-pattern='!develop' -Prelease-candidate -DskipTests -B -U;