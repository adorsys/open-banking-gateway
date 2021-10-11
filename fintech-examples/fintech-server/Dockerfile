FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*exec.jar .

EXPOSE 8086

ENTRYPOINT ["sh", "-c", "java -jar fintech-server-*.jar"]
