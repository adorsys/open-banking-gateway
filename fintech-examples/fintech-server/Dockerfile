FROM openjdk:8-jre-alpine

ARG JAR_FILE
ENV JAR_FILE ${JAR_FILE}
ENV APP_HOME /usr/app

WORKDIR $APP_HOME

COPY target/${JAR_FILE} ${JAR_FILE}

ENTRYPOINT ["sh", "-c", "java -jar \"${JAR_FILE}\""]
