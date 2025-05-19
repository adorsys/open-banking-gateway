# This Dockerfile is for deployment
FROM eclipse-temurin:21-jre-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*exec.jar .

EXPOSE 8090

ENTRYPOINT ["sh", "-c", "java -jar hbci-sandbox-*exec.jar"]