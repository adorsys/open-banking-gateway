# Consume API after Consent Authorization

## Description
This workflow starts with the redirect link leaving from either [Authorize Consent Redirect Approach](5b-psuAuthRedirectConsent.md) or [Authorize Consent Embedded Approach](5a-psuAuthEmbeddedConsent.md). This redirect link will be used by the ConsetAuthorizeUI to start the FinTechUI. 

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/6-consume_api.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### BankingService-001 : FinTechUI.loadFinTechUI
Receiving a 302_RedirectToFintech from the ConsentAuthorisationAPI, the ConsentAuthorisationUI starts the FinTechUI using the location param.

### BankingService-002 : FinTechUI.readXsrfToken
The FinTechUI uses the provided auth-id to load the xsrfToken stored for the corresponding auth-id.

### BankingService-002 : FinTechUI.buildBackendUrl
FinTechUI uses the provided auth-id to build the backendUrl used to forward the request to the FinTechAPI. The url has the form: /v1/{auth-id}/fromConsentOk.


### BankingService-010 : FinTechApi.fromConsentOk
The FinTechAPI uses the provided xsrfToken and RedirectCookie to legitimate the redirect request.

### BankingService-020 : FinTechApi.validateRedirectCall
The finTechConsentSessionState will be used to read and validate the corresponding FinTechConsentSessionCookie. 

### BankingService-030 : TppBankingApi.code2Token
This end point is invoked by the FinTechApi to retrieve token used to send subsequent service requests to the TppBankingApi. We call this token PsuConsentSession.
  
### BankingService-040 : BankingProtocol.code2Token
Forward request to banking protocol.

### BankingService-048 : FinTechApi.storePsuConsent
The returned PsuConsentSession is stored by the FinTechApi for future use.

### BankingService-049 : FinTechUI: 202_ReloadUI
At the end of the consent process, the FinTechAPI issues a redirect to the FinTechUI to display the original page to the user. 

### BankingService-050 : FinTechApi.listOfTransactions
Upon displaying the original page, the FinTechUI can reissue the original request to the the FinTechAPI.

### BankingService-060 .. BankingService-067 Service Requests
Regular execution of the banking service.
