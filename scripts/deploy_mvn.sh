#!/usr/bin/env bash
set -e

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml deploy -Prelease -DskipTests -B -U;
mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml nexus-staging:close -Prelease -DskipTests -B -U;
