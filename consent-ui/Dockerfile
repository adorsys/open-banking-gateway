### Stage 1: Build Angular app
FROM node:22-alpine as build

WORKDIR /app
COPY package*.json ./
RUN HUSKY_SKIP_INSTALL=1 npm i --silent

COPY . .
RUN npm run build -- --output-path=dist/consent-ui --configuration=production

### Stage 2: Nginx server
FROM adorsys/nginx:ubi
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"

COPY nginx.conf /etc/nginx/nginx.conf.template
COPY entry.sh /opt/entry.sh

RUN chgrp -R root /var/cache/nginx && \
    find /var/cache/nginx -type d -exec chmod 775 {} \; && \
    find /var/cache/nginx -type f -exec chmod 664 {} \; && \
    chmod 775 /var/run && \
    chmod -R 777 /etc/nginx && \
    chmod 775 /opt/entry.sh

COPY --from=build /app/dist/consent-ui/browser /app/dist

EXPOSE 4200

ENTRYPOINT ["/opt/entry.sh"]
