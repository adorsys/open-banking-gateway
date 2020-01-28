FROM openjdk:8-jre-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*.jar .

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java -jar open-banking-gateway-*.jar"]
