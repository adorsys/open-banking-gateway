#!/usr/bin/env bash
set -e

# Install custom plugin version to avoid catalog.txt deployment
mvn install:install-file -Dfile=scripts/plugins/gitflow-helper-maven-plugin-3.0.1-SKIP-CATALOG-SNAPSHOT.jar -DpomFile=scripts/plugins/gitflow-helper-maven-plugin-3.0.1-SKIP-CATALOG-SNAPSHOT-pom.xml || exit 1

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml deploy nexus-staging:release -Prelease -DskipTests -B -U
