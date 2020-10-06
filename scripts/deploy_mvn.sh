#!/usr/bin/env bash
set -e

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml gitflow-helper:promote-master gpg:sign nexus-staging:release -Prelease -DskipTests -B -U
