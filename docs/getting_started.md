# How to start the project

* Compile everything with `make all`. 
    * Alternatively compile Java code with `mvn clean package` and frontend with `npm build` at `fintech-examples/fintech-ui`
* Start frontends with `npm serve` from `fintech-examples/fintech-ui`
* Start backends with `mvn spring-boot:run` from `opba-embedded-starter`

# Adding new modules

When adding new modules or changing other modules ensure you are adding them to last-module-codecoverage too.