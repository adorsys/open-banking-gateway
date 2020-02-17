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
  1. `cd technical-ui`
  1. `01.build.sh` - build docker images of required infrastructure
  1. `02.start-dev-mode.sh` or `docker-compose up -e PROFILES=dev,no-encryption` in current directory - start the project (in development mode)