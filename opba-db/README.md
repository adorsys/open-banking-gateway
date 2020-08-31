# Database population with production data

## Populate database with data from CSV file

To facilitate database population with bank data, `BankProtocolActionsSqlGeneratorTest.java` class contains a script that 
generates all neccassary data, that can be used by liquibase plugin. The instruction how to use this script is the following:

* add or replace your bank data to the `./opba-db/src/main/resources/migration/migrations/banks.csv` 
file, with the same format it is now;
* manually run script `BankProtocolActionsSqlGeneratorTest.java#convertToDbSql` script. It will generate 3 CSV files in 
the samedirectory with `banks.csv`: `bank_action_data.csv` `bank_profile_data.csv` and `bank_sub_action_data.csv`. This 
files are a data source for liquibase changesets  `./opba-db/src/main/resources/migration/migrations/0003-add-staging-bank-configuration.xml`;
* set liquibase context value to the `prod`: in `application.yml` add `spring.liquibase.contexts=dev`. This will enable 
liquibase script to run during the OBG application start;
* run Open Banking Gateway.

CSV generation script is written as a Spring Boot Test and is disabled by default. To enable it, please set
`ENABLE_BANK_PROTOCOL_ACTIONS_SQL_GENERATION` environment variable to `true`.