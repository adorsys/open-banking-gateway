# Use erb to use conditions and environment variables in nginx file
erb /etc/nginx/conf.d/default.conf.erb > /etc/nginx/conf.d/default.conf

# Start nginx to server frontend application
nginx -g "daemon off;"
