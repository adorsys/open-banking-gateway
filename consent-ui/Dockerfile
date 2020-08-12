#############
### build ###
#############

# base image
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"

# set working directory
WORKDIR /app

# install and cache app dependencies
COPY package*.json /app/
RUN HUSKY_SKIP_INSTALL=1 npm ci --silent

# add app
COPY . /app/

RUN npm run build -- --output-path=dist && chown -R 1001:0 dist

FROM nginx:1.17.7-alpine
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"
COPY nginx.conf /etc/nginx/nginx.conf.template
COPY entry.sh /opt/entry.sh
RUN chgrp -R root /var/cache/nginx && \
    find /var/cache/nginx -type d -exec chmod 775 {} \; && \
    find /var/cache/nginx -type f -exec chmod 664 {} \; && \
    chmod 775 /var/run && \
    chmod -R 777 /etc/nginx && \
    chmod 775 /opt/entry.sh
COPY dist /app/dist
EXPOSE 4200
ENTRYPOINT /opt/entry.sh
