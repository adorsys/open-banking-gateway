#!/usr/bin/env bash

SCRIPT_DIR="$(dirname "$0")"

bash "$SCRIPT_DIR/promote_oc_image_to_dockerhub.sh"
bash "$SCRIPT_DIR/deploy_mvn.sh"