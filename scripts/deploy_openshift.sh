#!/usr/bin/env bash

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

IMAGE_TAG=${TRAVIS_COMMIT:0:7}
REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway-dev
SERVICE_NAME="open-banking-gateway"
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$IMAGE_TAG
LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:latest

docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" $REGISTRY_DOMAIN
JAR_NAME=open-banking-gateway-$PROJECT_VERSION.jar
docker build -t "$IMAGE_NAME" --build-arg JAR_FILE="$JAR_NAME" ./opba-embedded-starter
docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME"
docker push "$IMAGE_NAME"
docker push "$LATEST_IMAGE_NAME"

SERVICE_NAME_APP="fintech-ui"
IMAGE_NAME_APP=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME_APP:$IMAGE_TAG
LATEST_IMAGE_NAME_APP=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME_APP:latest
docker build -t "$IMAGE_NAME_APP" ./fintech-examples/fintech-ui
docker tag "$IMAGE_NAME_APP" "$LATEST_IMAGE_NAME_APP"
docker push "$IMAGE_NAME_APP"
docker push "$LATEST_IMAGE_NAME_APP"
