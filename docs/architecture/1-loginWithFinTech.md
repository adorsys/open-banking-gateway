# User Login

## Diagram 
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/1-loginWithFinTech.puml&fmt=svg&vvv=1&sanitize=true)  

## Description

### Login-001, -002, -003 FinTechUI.loadFinTechApplication & enterLoginData
FinTechUI displays the LoginUI to the PSU. PSU enters username and password and initiates the login request.
In a productive use case, the login request will be handled by an identity provider. 

### Login-004, -005 FinTechApi.login
The PSU initiates a session with the FinTech providing his username and password as known to the FinTechApi. Username and password are provided in a LoginRequest object.
Upon successful login, the FinTechApi will return a response of type [200_UserProfile](https://github.com/adorsys/open-banking-gateway/blob/develop/fintech-examples/fintech-api/src/main/resources/static/fintech_api.yml#200_UserProfile).
Note that the response object contains both a [SessionCookie](https://github.com/adorsys/open-banking-gateway/blob/develop/fintech-examples/fintech-api/src/main/resources/static/fintech_api.yml#Set-SessionCookie) 
and a corresponding [XSRF-TOKEN](https://github.com/adorsys/open-banking-gateway/blob/develop/fintech-examples/fintech-api/src/main/resources/static/fintech_api.yml#X-XSRF-TOKEN)

### Login-006 FinTechUI.parseAndStoreXsrfToken
The xsrfToken returned by the server must be parsed and stored by the FinTechUI for association with each subsequent request. The FinTechUI must 
parse and store this xsrfToken, so that it is accessible whenever the application is reloaded.

### Login-007 FinTechUI.displayBankSearchScreen
Upon successful login, the FinTechUi displays the bank search screen to the PSU.