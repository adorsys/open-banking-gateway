#!/usr/bin/env bash

SCRIPT_DIR="$(dirname "$0")"
IMAGE_TAG=${GITHUB_SHA:0:7}
REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway-dev
SERVICE_LIST_FILE="$SCRIPT_DIR/service.list"

if [[ -n "$1" ]]; then
    SERVICE_LIST_FILE="$1"
    echo "Using custom service.list $SERVICE_LIST_FILE"
fi

docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" $REGISTRY_DOMAIN || exit 1

while IFS="" read -r service_and_context || [ -n "$service_and_context" ]
do
    SERVICE_NAME=$(echo "$service_and_context" | cut -d"=" -f1)
    CONTEXT=$(echo "$service_and_context" | cut -d"=" -f2)
    echo "Deploying $SERVICE_NAME from $CONTEXT"

    IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$IMAGE_TAG
    LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:latest
    docker build -t "$IMAGE_NAME" "$CONTEXT" || exit 1
    docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME" || exit 1
    docker push "$IMAGE_NAME" || exit 1
    docker push "$LATEST_IMAGE_NAME" || exit 1
done < "$SERVICE_LIST_FILE"
