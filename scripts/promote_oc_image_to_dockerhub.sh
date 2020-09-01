#!/usr/bin/env bash

echo "Docker image promotion..."

SCRIPT_DIR="$(dirname "$0")"
SOURCE_IMAGE_TAG=${GITHUB_SHA:0:7}
GITHUB_TAG=${GITHUB_REF#refs/tags/}
TARGET_IMAGE_TAG="${GITHUB_TAG#v}" # Strip leading 'v' from image tag
SOURCE_REGISTRY_DOMAIN="$RELEASE_CANDIDATE_DOMAIN"
SOURCE_PROJECT_NAME="$RELEASE_CANDIDATE_PROJECT_NAME"
TARGET_PROJECT_NAME=adorsys
TARGET_REGISTRY_DOMAIN=docker.io

if [ -z "$TARGET_IMAGE_TAG" ]; then
  echo "No commit tag, aborting image promotion"
  exit 1
fi

echo "Pulling already existing images"
docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" "$SOURCE_REGISTRY_DOMAIN" || exit 1
while IFS="" read -r service_and_context || [ -n "$service_and_context" ]
do
    SERVICE_NAME=$(echo "$service_and_context" | cut -d"=" -f1)
    SOURCE_IMAGE_NAME="$SOURCE_REGISTRY_DOMAIN/$SOURCE_PROJECT_NAME/$SERVICE_NAME:$SOURCE_IMAGE_TAG"
    echo "Pulling $SERVICE_NAME from $SOURCE_IMAGE_NAME"
    docker pull "$SOURCE_IMAGE_NAME"
done < "$SCRIPT_DIR/service.list"

echo "Promoting pulled images"
docker login -u "$DOCKERHUB_USER" -p "$DOCKERHUB_PASS" "$TARGET_REGISTRY_DOMAIN" || exit 1
while IFS="" read -r service_and_context || [ -n "$service_and_context" ]
do
    SERVICE_NAME=$(echo "$service_and_context" | cut -d"=" -f1)
    SOURCE_IMAGE_NAME="$SOURCE_REGISTRY_DOMAIN/$SOURCE_PROJECT_NAME/$SERVICE_NAME:$SOURCE_IMAGE_TAG"
    # DockerHub does not support layered names - only namespace/project, so targeting adorsys/open-banking-gateway-open-banking-gateway, adorsys/open-banking-gateway-fintech-server, etc.
    TARGET_IMAGE_NAME="$TARGET_REGISTRY_DOMAIN/$TARGET_PROJECT_NAME/open-banking-gateway-$SERVICE_NAME:$TARGET_IMAGE_TAG"
    docker tag "$SOURCE_IMAGE_NAME" "$TARGET_IMAGE_NAME"
    echo "Promoting $SERVICE_NAME from $SOURCE_IMAGE_TAG to $TARGET_IMAGE_NAME"
    docker push "$TARGET_IMAGE_NAME"
done < "$SCRIPT_DIR/service.list"

echo "Done Docker images promotion"