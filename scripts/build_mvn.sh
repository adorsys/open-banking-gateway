#!/usr/bin/env bash

mvn verify --no-transfer-progress -Djgiven.report.text=false || exit 1
