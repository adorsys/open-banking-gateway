# User Login

## Diagram 
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/1-loginWithFinTech.puml&fmt=svg&vvv=1&sanitize=true)  

## Description

### Login-004 Sign In
The PSU initiate a session with the FinTech providing his username and password as known to the FinTech. Upon successful login, the FinTechApi will return a BankSearchScreenConfig and set a FinTechLoginSessionCookie.

Username and password  are provided in a FintechLoginRequest object. The request is also sent with a cookie used to maintain the login session between the FinTechUI and the FinTechApi (The FinTechLoginSessionCookie). If this cookie is sent, the FintechLoginRequest will contain a fintechSessionState used to check the cookie agains CSRF.
