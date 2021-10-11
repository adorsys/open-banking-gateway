# TPP Server Protocol Testing helper

Exposes protocol as REST API, so that one can play with it easier.

```
> mvn clean install
> mvn spring-boot:run

then http://localhost:8087
```

# Hot reload - changing classes without application restart

As you know - using `spring-boot-dev-tools` you can avoid application restart and do just recompilation of changed 
classes - all your changes will be picked automatically.
This may cause problems with breakpoints during debugging and to fix them: 

- For proper hot reload in Debugging session (if breakpoints can't be hit in IntelliJ):
  Launch application with argument (Program Argument):
```
-Dspring-boot.run.fork=false
```
