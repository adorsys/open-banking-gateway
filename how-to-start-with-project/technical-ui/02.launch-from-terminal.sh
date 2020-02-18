#!/bin/bash

LOGS_SANDBOX=/tmp/sandbox.log
LOGS_OPBA=/tmp/open-banking.log
LOGS_DOCKER=/tmp/sandbox-docker.log

trap "docker logs opba-technical-ui >'$LOGS_DOCKER'; docker rm -f opba-pg-docker; docker rm -f opba-technical-ui ;trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT

PROJECT_ROOT=`pwd`"/../.."

echo ">>> 1. Starting Open-banking POSTGRES DATABASE"
docker run --rm --name opba-pg-docker -e POSTGRES_DB=open_banking -e POSTGRES_PASSWORD=docker -d -p 5432:5432 \
  -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data \
  -v "$PROJECT_ROOT/opba-db/src/main/resources/init.sql":/docker-entrypoint-initdb.d/init.sql \
  postgres

echo ">>> 3. Starting Technical UI"
# host.docker.internal not supported on Linux :(
if [[ "$(uname)" == "Darwin" ]]; then
    docker run --rm --name opba-technical-ui -e TPP_BANKING_UI_HOST_AND_PORT=localhost:4400 -e TECHNICAL_UI_HOST_AND_PORT=localhost:5500 -e EMBEDDED_SERVER_URL="http://docker.host.internal:8085" -d -p 5500:5500 -v "$PROJECT_ROOT/technical-embedded-ui/tpp-ui":/usr/src/app technical-tpp-ui
elif [[ "$(expr substr $(uname -s) 1 5)" == "Linux" ]]; then
    docker run --rm --name opba-technical-ui --network=host -e TPP_BANKING_UI_HOST_AND_PORT=localhost:4400 -e TECHNICAL_UI_HOST_AND_PORT=localhost:5500 -e EMBEDDED_SERVER_URL="http://localhost:8085" -d -p 5500:5500 -v "$PROJECT_ROOT/technical-embedded-ui/tpp-ui":/usr/src/app technical-tpp-ui
fi


cd "$PROJECT_ROOT" || exit

echo ">>> 4. Starting Sandbox"
export ENABLE_HEAVY_TESTS=true
./mvnw -DSTART_SANDBOX=true -Dtest=de.adorsys.opba.protocol.xs2a.testsandbox.BasicTest#startTheSandbox test -pl opba-protocols/sandboxes/xs2a-sandbox &>"$LOGS_SANDBOX" &
echo "SANDBOX HAS PID: [$!], watch $LOGS_SANDBOX for logs"
echo "SANDBOX HAS PID: [$!]" >> "$LOGS_SANDBOX"

echo ">>> 5. Starting Open-Banking backend (profiles dev,no-encryption,technical-ui)"
./mvnw -Prun-embedded -pl opba-embedded-starter -am spring-boot:run -Dspring-boot.run.profiles=dev,no-encryption,technical-ui -DskipTests &>"$LOGS_OPBA" &
echo "OPEN-BANKING HAS PID: [$!], watch $LOGS_OPBA for logs"
echo "OPEN-BANKING HAS PID: [$!]" >> "$LOGS_OPBA"

echo ">>> 6. Waiting for apps to start"
# NOP for now...
sleep 60

echo ">>> 7. Open http://localhost:5500/initial - applications should be running (but might be still launching), Ctrl+C to terminate"
sleep infinity