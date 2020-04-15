To create the initial file

drop database
go to application.yml of server
change to ddl-auto: create-drop
start application
stop application
revert change in application.yml

(download and install liquibase software (its just an extraction of a jar))
go to liqubase directory
create liqubase.properties with the following content

driver: org.postgresql.Driver
url: jdbc:postgresql://localhost/open_banking?currentSchema=fintech
username: postgres
password: docker
classpath: /Users/peter/.m2/repository/org/postgresql/postgresql/42.2.11/postgresql-42.2.11.jar

run the following command
./liquibase --changeLogFile=/tmp/0000-init-schema.xml generateChangeLog
sed "s#type=\"TIMESTAMP without time zone\"#type=\"TIMESTAMP\"#g" /tmp/0000-init-schema.xml
