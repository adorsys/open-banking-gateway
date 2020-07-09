#!/usr/bin/env sh

# Use erb to use conditions and environment variables in nginx file
erb /etc/nginx/conf.d/default.conf.erb >/etc/nginx/conf.d/default.conf

# Continue with normal start
. /docker-entrypoint.sh
