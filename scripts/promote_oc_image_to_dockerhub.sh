#!/usr/bin/env bash

echo "Docker image promotion..."

SOURCE_IMAGE_TAG=${TRAVIS_COMMIT:0:7}
TARGET_IMAGE_TAG=$TRAVIS_TAG
SOURCE_REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway-dev
TARGET_REGISTRY_DOMAIN=openshift-registry.adorsys.de

if [ -z "$TARGET_IMAGE_TAG" ]; then
  echo "No commit tag, aborting image promotion"
  exit 1;
fi

echo "Pulling already existing images"
docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" "$SOURCE_REGISTRY_DOMAIN"
while IFS="" read -r service_and_context || [ -n "$service_and_context" ]
do
    SERVICE_NAME=$(echo "$service_and_context" | cut -d"=" -f1)
    SOURCE_IMAGE_NAME="$SOURCE_REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$SOURCE_IMAGE_TAG"
    echo "Pulling $SERVICE_NAME from $SOURCE_IMAGE_NAME"
    docker pull "$SOURCE_IMAGE_NAME"
done < service.list

echo "Promoting pulled images"
docker login -u "$DOCKERHUB_USER" -p "$DOCKERHUB_PASS" "$TARGET_REGISTRY_DOMAIN"
while IFS="" read -r service_and_context || [ -n "$service_and_context" ]
do
    SERVICE_NAME=$(echo "$service_and_context" | cut -d"=" -f1)
    SOURCE_IMAGE_NAME="$SOURCE_REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$SOURCE_IMAGE_TAG"
    TARGET_IMAGE_NAME="$TARGET_REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$TARGET_IMAGE_TAG"
    docker tag "$SOURCE_IMAGE_NAME" "$TARGET_IMAGE_NAME"
    echo "Promoting $SERVICE_NAME from $SOURCE_IMAGE_TAG to $TARGET_IMAGE_NAME"
    docker push "$TARGET_IMAGE_NAME"
done < service.list

echo "Done Docker images promotion"