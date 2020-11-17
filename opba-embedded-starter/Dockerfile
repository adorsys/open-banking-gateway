FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*exec.jar .

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java -jar open-banking-gateway-*exec.jar"]
