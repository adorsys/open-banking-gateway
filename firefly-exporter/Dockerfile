FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*.jar .

EXPOSE 9001

ENTRYPOINT ["sh", "-c", "java -jar fireflyexporter-*.jar"]
