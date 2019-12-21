#!/usr/bin/env bash

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

IMAGE_TAG=${TRAVIS_COMMIT:0:7}
REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway-dev
SERVICE_NAME="tpp-bank-search-api"
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:$IMAGE_TAG
LATEST_IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME:latest

docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" $REGISTRY_DOMAIN
JAR_NAME=banking-protocol-$PROJECT_VERSION.jar
docker build -t "$IMAGE_NAME" --build-arg JAR_FILE="$JAR_NAME" ./core/banking-protocol
docker tag "$IMAGE_NAME" "$LATEST_IMAGE_NAME"
docker push "$IMAGE_NAME"
docker push "$LATEST_IMAGE_NAME"
