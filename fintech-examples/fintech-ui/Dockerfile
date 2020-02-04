#############
### build ###
#############

# base image
FROM node:12.14.0 as build-stage
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"

# set working directory
WORKDIR /app

# install and cache app dependencies
COPY package*.json /app/
RUN HUSKY_SKIP_INSTALL=1 npm ci --silent

# add app
COPY . /app/

# build arguments
ARG configuration=production

# environment variables
ENV NGINX_ACCESS_LOG_DEBUG combined
ENV NGINX_ERROR_LOG_DEBUG error

# generate build
RUN npm run build -- --output-path=./dist --configuration $configuration \
    && chown -R 1001:0 ./dist

############
### prod ###
############

# base image
FROM adorsys/nginx:ubi
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"


# copy artifact build from the 'build environment'
COPY --from=build-stage /app/dist /usr/share/nginx/html

USER 0

# copy nginx configuration
COPY docker-root /

# install ruby
RUN  microdnf install --nodocs -y ruby && microdnf clean all \
    && chown -R 1001:0 /etc/nginx/conf.d /entry.sh \
    && chmod 555 /entry.sh \
    && chmod 777 -R /etc/nginx/conf.d

USER 1001


# expose port 4200
EXPOSE 4200

# execute entry.sh file
ENTRYPOINT ["/entry.sh"]

CMD ["nginx", "-g", "daemon off;"]
