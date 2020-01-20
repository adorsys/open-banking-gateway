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
RUN npm run build -- --output-path=./dist --configuration $configuration

############
### prod ###
############

# base image
FROM nginx:1.17.6
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"

# install ruby
RUN \
  apt-get update && \
  apt-get install -y ruby

# copy artifact build from the 'build environment'
COPY --from=build-stage /app/dist /usr/share/nginx/html

# copy nginx configuration
COPY ./nginx.conf.erb /etc/nginx/conf.d/default.conf.erb

# copy entry.sh file
COPY ./entry.sh /opt/entry.sh

# support running as arbitrary user which belogs to the root group
RUN chmod g+rwx /var/cache/nginx /var/run /var/log/nginx && \
    chmod 775 /opt/entry.sh && \
    chmod -R 777 /etc/nginx/conf.d

# comment user directive as master process is run as user in OpenShift anyhow
RUN sed -i.bak 's/^user/#user/' /etc/nginx/nginx.conf

# expose port 4200
EXPOSE 4200

# execute entry.sh file
ENTRYPOINT /opt/entry.sh
