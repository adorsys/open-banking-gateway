#!/usr/bin/env bash
set -e

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml deploy nexus-staging:release -Prelease -DskipTests -B -U;
