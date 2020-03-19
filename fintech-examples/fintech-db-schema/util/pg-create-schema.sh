#!/usr/bin/env bash

# create fintech schema and give permissions to postgres user. Needed for
# docker-compose so  that we can start the DB with the schema already being
# present (rat)
set -e
echo "Create schema='fintech' for local postgres installation"
psql -U postgres -d fintech -c 'CREATE SCHEMA IF NOT EXISTS fintech AUTHORIZATION postgres;'