#!/bin/bash

DIR=`pwd`
PROJECT_ROOT=`pwd`"/../.."

cd "$PROJECT_ROOT" || exit

echo ">>> 01. Build Open Banking Jars"
./mvnw clean package -DskipTests || exit

echo ">>> 02. Build Technical UI"
# MacOS docker volumes are sloow...
if [[ "$(uname)" == "Darwin" ]]; then
    docker run -it --rm --name technical-ui-build -v "$PROJECT_ROOT/technical-embedded-ui/tpp-ui":/usr/src/app:delegated -w /usr/src/app node:12.6.0-stretch npm install || exit
elif [[ "$(expr substr $(uname -s) 1 5)" == "Linux" ]]; then
    docker run -it --rm --name technical-ui-build -v "$PROJECT_ROOT/technical-embedded-ui/tpp-ui":/usr/src/app -w /usr/src/app node:12.6.0-stretch npm install || exiti
fi



echo ">>> 04. Build Open Banking docker image"
docker build "$PROJECT_ROOT/opba-embedded-starter" -t open-banking-gateway || exit

echo ">>> 03. Build Technical UI docker image"
docker build "$PROJECT_ROOT/technical-embedded-ui/tpp-ui" -t technical-tpp-ui || exit

cd "$DIR" || exit