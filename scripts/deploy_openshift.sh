#!/usr/bin/env bash

curl -L https://github.com/openshift/origin/releases/download/v3.11.0/openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit.tar.gz > oc-cli.tar.gz
tar -xzf oc-cli.tar.gz
sudo mv ./openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/oc /usr/local/bin

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
oc login https://openshift.adorsys.de --token="$OPENSHIFT_TOKEN"


IMAGE_TAG=${TRAVIS_COMMIT:0:7}
REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway-dev
SERVICE_NAME="tpp-bank-search-api"
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME

docker login -u github-image-pusher -p "$OPENSHIFT_TOKEN" $REGISTRY_DOMAIN
JAR_NAME=banking-protocol-$PROJECT_VERSION.jar
docker build -t $IMAGE_NAME:"$IMAGE_TAG" --build-arg JAR_FILE="$JAR_NAME" ./core/banking-protocol
docker push $IMAGE_NAME:"$IMAGE_TAG" && \
oc tag $PROJECT_NAME/$SERVICE_NAME:"$IMAGE_TAG" $PROJECT_NAME/$SERVICE_NAME:latest
