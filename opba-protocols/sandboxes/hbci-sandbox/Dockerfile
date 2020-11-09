# This Dockerfile is for deployment
FROM openjdk:11.0.8-jre-slim-buster

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*exec.jar .

EXPOSE 8090

ENTRYPOINT ["sh", "-c", "java -jar hbci-sandbox-*exec.jar"]