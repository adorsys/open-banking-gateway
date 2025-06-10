#!/usr/bin/env bash

SCRIPT_DIR="$(dirname "$0")"
IMAGE_TAG=${GITHUB_SHA:0:7}  # or `git rev-parse --short HEAD` if not in GitHub Actions
REGISTRY_DOMAIN=ghcr.io
GITHUB_OWNER=adorsys
SERVICE_LIST_FILE="$SCRIPT_DIR/service.list"

# Determine if this is a develop build
IS_DEVELOP=false
if [[ "$GITHUB_REF" == "refs/heads/develop" ]] || [[ "$1" == "develop" ]]; then
    IS_DEVELOP=true
    echo "Building DEVELOP images with classifier"
else
    echo "Building RELEASE images"
fi

# Override service list if provided as argument (but not if it's "develop")
if [[ -n "$1" && "$1" != "develop" ]]; then
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

    if [[ "$IS_DEVELOP" == "true" ]]; then
        # Develop images get -develop classifier
        IMAGE_NAME=$REGISTRY_DOMAIN/$GITHUB_OWNER/$SERVICE_NAME:$IMAGE_TAG-develop
        LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$GITHUB_OWNER/$SERVICE_NAME:latest-develop
        echo "  -> Tagging as develop build: $IMAGE_NAME"
    else
        # Release/tag images get clean names
        IMAGE_NAME=$REGISTRY_DOMAIN/$GITHUB_OWNER/$SERVICE_NAME:$IMAGE_TAG
        LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$GITHUB_OWNER/$SERVICE_NAME:latest
        echo "  -> Tagging as release build: $IMAGE_NAME"
    fi

    docker build -t "$IMAGE_NAME" "$CONTEXT" || exit 1
    docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME" || exit 1
    docker push "$IMAGE_NAME" || exit 1
    docker push "$LATEST_IMAGE_NAME" || exit 1
done < "$SERVICE_LIST_FILE"
