FROM openjdk:11.0.8-jre-slim-buster

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*.jar .

EXPOSE 9001

ENTRYPOINT ["sh", "-c", "java -jar fireflyexporter-*.jar"]
