## **Roadmap**

Our project started on November 1st 2019 and the Open Banking Gateway team offers development with two branches: 

* **Develop branch** : This is the branch from which a release is built and used to collect all the other branches. 
* **Master branch**: generally matches the last released stable version.

This project is planned until the end of 2020 and our current timetable plans MVP1 for April 2020, by which time the following targets should be achieved:
* Demo Frontend used to test the Open Banking Gateway
* Integration of German banks with **Redirect** and **Embedded** Approaches 
* **TppBankSearchApi** providing only German banks 
* **TppBankingApi** providing the following banking endpoints:
    * Get transaction information
    * Get list of reachable accounts
    * Get account details of a list of accessible accounts
    * Get balances for a given account and 
    * Initiation of single payment

| release date       | version           |
| ------------- |:-------------|
| ***20.12.2019***      | ***0.0.3*** | 
|            |  Sequence diagram describing use cases<br/><br/>Validation of  flowable BPMN engine<br/><br/>Backend Proof of Concept <br/><br/>Implementation of TppBankSearchApi <br/><br/>Create CI/CD and code quality checks <br/><br/>Write Contributions guidelines| 
|***16.01.2020***     |***0.0.4***    |
|            |Login Page creation for the Demo Frontend<br/><br/>Change Test data with productive data for the TppBankSearchApi<br/><br/>Update the general project's documentation<br/><br/>Getting Started documentation<br/><br/>Definition of the FinTechApi, BankingApi and ConsentAuthorisationApi<br/><br/>Integration of Bank search API and FintechUI<br/><br/>       |
| ***29.01.2020***      | ***0.0.5*** | 
|            |Move the implemented TppBankSearchApi to a separate module<br/><br/>Make a Backend for the login to the FinTechUi<br/><br/>Implementation of the Login Page<br/><br/>Dummy Implementation of the BankingApi *list of account* <br/><br/>Dummy Implementation of the *list of account* of the FinTechApi<br/><br/> Create relation between BankId and Service offered<br/><br/>Create Banking Protocol Facade<br/><br/>Implementation of the Banking Protocol for the dynamic Sandbox<br/><br/>Create the database migration module| 

