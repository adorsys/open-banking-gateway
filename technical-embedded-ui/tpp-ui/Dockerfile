FROM node:12.6.0-stretch

WORKDIR /usr/src/app
RUN npm install --silent -g @angular/cli@latest

CMD ["ng", "serve", "--port", "5500", "--host", "0.0.0.0"]
