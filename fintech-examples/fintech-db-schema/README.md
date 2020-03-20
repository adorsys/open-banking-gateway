# How to work with liquibase:

* Create new scheme 'fintech' in DB 

* Go to module fintech-db-schema in the project.

* Copy “liquibase.example.properties”, paste it in the same module near the old one and name it ” liquibase.properties”, so now you will have 2 “properties” files: “liquibase.properties” and ” liquibase.example.properties”.

* Run in the command line: 
```
 mvn liquibase:update
```

You should see the response “Build success” in the console after performing the update.

If you'd like to have different properties to connect to different databases, you may specify property file using the maven variable:
```
 mvn -DpropertyFile=my-special-property-file.properties liquibase:update
```


# How to deliver liquibase migrations

by invoking
```bash
mvn package
```
man build jar-file, that contains all migrations.
Adding this jar as a dependency to your project you get access to migration files and can use it with liquibase.
```xml
    <dependency>
        <groupId>de.adorsys.opba</groupId>
        <artifactId>fintech-db-schema</artifactId>
    </dependency>

```
