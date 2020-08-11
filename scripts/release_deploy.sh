#!/usr/bin/env bash

bash "$(dirname "$0")/promote_oc_image_to_dockerhub.sh"
bash "$(dirname "$0")/deploy_mvn.sh"