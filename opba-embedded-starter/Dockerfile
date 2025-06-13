FROM eclipse-temurin:21-jre-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*exec.jar .

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java -jar open-banking-gateway-*exec.jar"]
