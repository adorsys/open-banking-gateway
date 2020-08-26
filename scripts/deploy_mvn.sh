#!/usr/bin/env bash
set -e

mvn --no-transfer-progress --settings scripts/mvn-release-settings.xml gitflow-helper:retarget-deploy gitflow-helper:promote-master deploy -Prelease -DskipTests -B -U
