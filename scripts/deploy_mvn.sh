#!/usr/bin/env bash
set -e

# These are production secrets
echo "$GPG_SECRET_KEY" | base64 --decode | $GPG_EXECUTABLE --import --no-tty --batch --yes || true
echo "$GPG_OWNERTRUST" | base64 --decode | $GPG_EXECUTABLE --import-ownertrust --no-tty --batch --yes || true

# Install custom plugin version to avoid catalog.txt deployment
mvn install:install-file -Dfile=scripts/plugins/gitflow-helper-maven-plugin-3.0.1-SKIP-CATALOG-SNAPSHOT.jar -DpomFile=scripts/plugins/gitflow-helper-maven-plugin-3.0.1-SKIP-CATALOG-SNAPSHOT-pom.xml || exit 1

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml deploy -Prelease -DskipTests -B -U
