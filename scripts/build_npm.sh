#!/usr/bin/env bash

# docker run --rm -v "$PWD":/opt/app-root/src -w /opt/app-root/src -u "$(id -u "${USER}"):$(id -g "${USER}")" trion/ng-cli-karma make fintech-ui
# docker run --rm -v "$PWD":/opt/app-root/src -w /opt/app-root/src -u "$(id -u "${USER}"):$(id -g "${USER}")" trion/ng-cli-karma make consent-ui