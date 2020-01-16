#!/usr/bin/env bash
export DOLLAR='$'

erb /etc/nginx/conf.d/default.conf.erb > /etc/nginx/conf.d/default.conf

nginx -g "daemon off;"
