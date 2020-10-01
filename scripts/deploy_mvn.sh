#!/usr/bin/env bash
set -e

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml deploy -Prelease -DskipTests -B -U
