#!/usr/bin/env bash

SCRIPT_DIR="$(dirname "$0")"
IMAGE_TAG=${GITHUB_SHA:0:7}  # or `git rev-parse --short HEAD` if not in GitHub Actions
REGISTRY_DOMAIN=ghcr.io
GITHUB_OWNER=adorsys
SERVICE_LIST_FILE="$SCRIPT_DIR/service.list"

if [[ -n "$1" ]]; then
    SERVICE_LIST_FILE="$1"
    echo "Using custom service.list $SERVICE_LIST_FILE"
fi

# Login to GitHub Container Registry
echo "$GHCR_DEPLOY_TOKEN" | docker login ghcr.io -u "$GITHUB_OWNER" --password-stdin || exit 1

while IFS="" read -r service_and_context || [ -n "$service_and_context" ]
do
    SERVICE_NAME=$(echo "$service_and_context" | cut -d"=" -f1)
    CONTEXT=$(echo "$service_and_context" | cut -d"=" -f2)
    echo "Deploying $SERVICE_NAME from $CONTEXT"

    IMAGE_NAME=$REGISTRY_DOMAIN/$GITHUB_OWNER/$SERVICE_NAME:$IMAGE_TAG
    LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$GITHUB_OWNER/$SERVICE_NAME:latest

    docker build -t "$IMAGE_NAME" "$CONTEXT" || exit 1
    docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME" || exit 1
    docker push "$IMAGE_NAME" || exit 1
    docker push "$LATEST_IMAGE_NAME" || exit 1
done < "$SERVICE_LIST_FILE"
