FROM openjdk:8-jre-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*exec.jar .

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java -jar fireflyexporter-*.jar"]
