#!/usr/bin/env bash

docker run --rm -v "$PWD":/opt/app-root/src -w /opt/app-root/src -u "$(id -u "${USER}"):$(id -g "${USER}")" trion/ng-cli-karma:19.2.6 make fintech-ui || exit 1
docker run --rm -v "$PWD":/opt/app-root/src -w /opt/app-root/src -u "$(id -u "${USER}"):$(id -g "${USER}")" trion/ng-cli-karma:19.2.6 make consent-ui || exit 1
