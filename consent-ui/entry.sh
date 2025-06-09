#!/usr/bin/env sh
export DOLLAR='$'
export BACKEND_URL=${BACKEND_URL:-"http://localhost:8085"}
envsubst < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf
nginx -g "daemon off;"
