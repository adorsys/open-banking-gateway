#!/usr/bin/env bash
PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
oc login https://openshift.adorsys.de:443 --username="$OPENSHIFT_USER" --password="$OPENSHIFT_PASSWORD"
IMAGE_TAG=${TRAVIS_COMMIT:0:7}
REGISTRY_DOMAIN=openshift-registry.adorsys.de
PROJECT_NAME=open-banking-gateway
SERVICE_NAME=obg-bank-search-service
IMAGE_NAME=$REGISTRY_DOMAIN/$PROJECT_NAME/$SERVICE_NAME
docker login -u "$(oc whoami)" -p "$(oc whoami -t)" https://$REGISTRY_DOMAIN
JAR_NAME=banking-protokol-$PROJECT_VERSION.jar
docker build -t $IMAGE_NAME:"$IMAGE_TAG" --build-arg JAR_FILE="$JAR_NAME" ./core/banking-protocol
docker push $IMAGE_NAME:"$IMAGE_TAG" && \
oc tag $PROJECT_NAME/$SERVICE_NAME:"$IMAGE_TAG" $PROJECT_NAME/$SERVICE_NAME:latest
