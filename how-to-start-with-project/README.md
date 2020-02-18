# How to start with project
 
## Prerequisites

Ensure you have:
 1. Java 8+ JDK
 1. Docker
 1. Docker-compose
 1. For MacOS/Windows users ensure you have allocated at least 4Gb of RAM for docker (this is for XS2A-Sandbox ASPSP (bank) mock):
  - MacOS: https://stackoverflow.com/questions/32834082/how-to-increase-docker-machine-memory-mac
  - Windows: https://stackoverflow.com/questions/43460770/docker-windows-container-memory-limit

## Building and running:

### Without FinTech part using 'Technical UI' to drive backend:

This section is primarily for project-developers. 'Technical-UI' is the stub UI to drive backend.

### Application components:

 1. Open banking backend - `OpenBankingEmbeddedApplication` Spring-boot application.
 1. Open banking technical UI - `technical-embedded-ui` developer-only UI to drive backend.
 1. Sandbox (XS2A-Dynamic-Sandbox) that mocks ASPSP (bank).
 1. Postgres database (shared or separate (when using docker-compose) for Sandbox and Open banking backend)

### Run from IDE
In case you have `node.js (with npm), angular-cli` installed and want to run application directly from IDE, follow steps below

##### 1. Starting only Sandbox first

Sandbox can be started using this docker-compose file [sandbox-docker-compose](../how-to-start-with-project/sandbox-only/docker-compose.yml)

 `cd ../sandbox-only; docker compose up`

##### 2. Starting backend and technical-ui next

 1. Start Postgres: `docker run --rm --name opba-pg-docker -e POSTGRES_PASSWORD=docker -d -p 5432:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data postgres`
 This database will have admin user postgres/docker when started using aforementioned command and it will be available at `localhost:5432`.
 1. Prepare Postgres (should be done only once) - execute: [open-banking-init.sql](../opba-db/src/main/resources/init.sql) 
 and [sandbox-init.sql](../opba-protocols/sandboxes/xs2a-sandbox/src/main/resources/sandbox/prepare-postgres.sql)
 1. Run OpenBanking backend (Spring-boot application) [OpenBankingEmbeddedApplication](../opba-embedded-starter/src/main/java/de/adorsys/opba/starter/OpenBankingEmbeddedApplication.java) 
 with profiles `dev,no-encryption,technical-ui`
 1. Prepare and run technical-ui:
    - Install node modules at [tpp-ui](../technical-embedded-ui/tpp-ui/) 
    
    `cd ../technical-embedded-ui/tpp-ui; ng serve --port 5500`
    - Run technical-ui via [node package.json](../technical-embedded-ui/tpp-ui/package.json)
    
    ` ng serve --port 5500`
 
### Run from terminal

### Running with docker-compose

  1. `cd technical-ui`
  1. Run [01.build.sh](technical-ui/01.build.sh) - build docker images of the required infrastructure
  1. Run [02.launch-from-docker.sh](technical-ui/02.launch-from-docker.sh) or `docker-compose up -e OPBA_PROFILES=dev,no-encryption` in current directory - start the project (in development mode)
  1. Open `http://localhost:5500/initial` in your browser
  
### Running manually (sandbox starts in one JVM - less resource usage, also you can debug everything easier)
  
  1. `cd technical-ui`
  1. Run [01.build.sh](technical-ui/01.build.sh) - build docker images of the required infrastructure (just for technical UI to be served without NPM and Angular-CLI)
  1. Run [02.launch-from-terminal.sh](technical-ui/02.launch-from-terminal.sh)
  1. Open `http://localhost:5500/initial` in your browser
  
  **Or instead, same manually**:
  
  1. Start Postgres: `docker run --rm --name opba-pg-docker -e POSTGRES_PASSWORD=docker -d -p 5432:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data postgres`. 
  This database will have admin user postgres/docker when started using aforementioned command.
  1. Prepare Postgres (should be done only once) - execute: [open-banking-init.sql](../opba-db/src/main/resources/init.sql) 
  and [sandbox-init.sql](../opba-protocols/sandboxes/xs2a-sandbox/src/main/resources/sandbox/prepare-postgres.sql)
  1. Import maven project into your IDE. 
  1. Run [BasicTest#startTheSandbox](../opba-protocols/sandboxes/xs2a-sandbox/src/test/java/de/adorsys/opba/protocol/xs2a/testsandbox/BasicTest.java). 
  This 'test' simply starts entire sandbox jars in single JVM (so you can run this test in debug mode to see what happens in Sandbox).
  1. Run Spring-boot application [OpenBankingEmbeddedApplication](../opba-embedded-starter/src/main/java/de/adorsys/opba/starter/OpenBankingEmbeddedApplication.java) 
  with profiles `dev,no-encryption,technical-ui`
  1. Start UI with docker (since it needs to reach host and `docker.host.internal` is not supported everywhere):
   - MacOS:
   `docker run --rm --name opba-technical-ui -e TPP_BANKING_UI_HOST_AND_PORT=localhost:4400 -e TECHNICAL_UI_HOST_AND_PORT=localhost:5500 -e EMBEDDED_SERVER_URL="http://docker.host.internal:8085" -d -p 5500:5500 -v "$PROJECT_ROOT/technical-embedded-ui/tpp-ui":/usr/src/app technical-tpp-ui`
   - Linux:
    `docker run --rm --name opba-technical-ui --network=host -e TPP_BANKING_UI_HOST_AND_PORT=localhost:4400 -e TECHNICAL_UI_HOST_AND_PORT=localhost:5500 -e EMBEDDED_SERVER_URL="http://localhost:8085" -d -p 5500:5500 -v "$PROJECT_ROOT/technical-embedded-ui/tpp-ui":/usr/src/app technical-tpp-ui`
  1. Open `http://localhost:5500/initial` in your browser

## Notes

### Example of filled form: