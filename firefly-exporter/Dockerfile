FROM openjdk:11.0.9_alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*.jar .

EXPOSE 9001

ENTRYPOINT ["sh", "-c", "java -jar fireflyexporter-*.jar"]
