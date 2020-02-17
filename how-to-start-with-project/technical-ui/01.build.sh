#!/bin/bash

DIR=`pwd`

cd .. || exit

echo "Build Open Banking Jars"
./mvnw clean package -DskipTests

echo "Build Technical UI"

echo "Build Technical UI docker image"

echo "Build Open Banking docker image"

cd "$DIR" || exit