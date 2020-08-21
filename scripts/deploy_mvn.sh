#!/usr/bin/env bash
set -e

echo "$GPG_SECRET_KEY" | base64 --decode | $GPG_EXECUTABLE --import || true
echo "$GPG_OWNERTRUST" | base64 --decode | $GPG_EXECUTABLE --import-ownertrust || true
mvn --settings scripts/mvn-release-settings.xml package gpg:sign deploy -Prelease -DskipTests -B -U;
