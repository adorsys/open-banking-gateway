# User Login

## Diagram 
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/1-loginWithFinTech.puml&fmt=svg&vvv=1&sanitize=true)  

## Description

### Login-001, -002, -003 FinTechUI.loadFinTechApplication & enterLoginData
FinTechUI displays the LoginUI to the PSU. PSU enters username and password and initiates the login request.

### Login-004, -005 FinTechApi.login
The PSU initiate a session with the FinTech providing his username and password as known to the FinTechApi.
Username and password are provided in a LoginRequest object. Upon successful login, the FinTechApi will return a response 200_UserProfile containing:
* UserProfile: provided in the body.
* sessionState: provided in the body. Used read the FinTechLoginSessionCookie. 
* FinTechLoginSessionCookie: provided in the Set-Cookie header.

### Login-006 FinTechUI.displayBankSearchScreen
Upon successfull login, the FinTechUi displays the bank search screen to the PSU.