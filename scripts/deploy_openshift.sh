#!/usr/bin/env bash

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

IMAGE_TAG=${TRAVIS_COMMIT:0:7}
REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway-dev

docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" $REGISTRY_DOMAIN

SERVICE_NAME="open-banking-gateway"
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$IMAGE_TAG
LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:latest
docker build -t "$IMAGE_NAME" ./opba-embedded-starter
docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME"
docker push "$IMAGE_NAME"
docker push "$LATEST_IMAGE_NAME"

SERVICE_NAME="fintech-server"
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$IMAGE_TAG
LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:latest
docker build -t "$IMAGE_NAME" ./fintech-examples/fintech-server
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

SERVICE_NAME_APP="consent-ui"
IMAGE_NAME_APP=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME_APP:$IMAGE_TAG
LATEST_IMAGE_NAME_APP=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME_APP:latest
docker build -t "$IMAGE_NAME_APP" ./consent-ui
docker tag "$IMAGE_NAME_APP" "$LATEST_IMAGE_NAME_APP"
docker push "$IMAGE_NAME_APP"
docker push "$LATEST_IMAGE_NAME_APP"

SERVICE_NAME="hbci-sandbox-server"
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$IMAGE_TAG
LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:latest
docker build -t "$IMAGE_NAME" ./opba-protocols/sandboxes/hbci-sandbox
docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME"
docker push "$IMAGE_NAME"
docker push "$LATEST_IMAGE_NAME"
