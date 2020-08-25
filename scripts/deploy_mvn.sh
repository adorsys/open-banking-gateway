#!/usr/bin/env bash
set -e

mvn --settings scripts/mvn-release-settings.xml nexus-staging-maven-plugin:deploy -Prelease -DskipTests -B -U;
