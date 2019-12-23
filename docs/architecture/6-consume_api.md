# Consume API after Consent Authorization

## Description
This workflow starts with the redirect link leaving from either [Authorize Consent Redirect Approach](5b-psuAuthRedirectConsent.md) or [Authorize Consent Embedded Approach](5a-psuAuthEmbeddedConsent.md). This redirect link will be used by the FinTechApi to retrieve a corresponding Token that can be used to request services on behalf of the PSU.

As long as this token is valid, token will be used to perform corresponding service request on behalf on the PSU. 

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/6-consume_api.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### BankingService-010 : FinTechApi.fromConsentOk
The redirect request coming from the [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi) contains a redirectCode. This request will be forwarded by the FinTechApi to the TppBankingApi. The request contains following information:

#### redirectCode
Available in the redirect url. This information will be used to retrieve the authorization token from the TppBankingApi. So the information needs not be processed by the FinTechApi

#### FinTechConsentSessionCookie
Available in the request header. This cookie shall be set for the Max time given to the PSU for the authorization of the corresponding consent. The cookie can be bound to the end point FinTechApi.fromConsentOk so it does not need to be transported to the server on other requests.

#### finTechConsentSessionState
Available in the redirect url. Will be used to read and validate the corresponding FinTechConsentSessionCookie.

### BankingService-020 : Validate the redirectLink
The finTechConsentSessionState will be used to read and validate the corresponding FinTechConsentSessionCookie. 

### BankingService-030 : TppBankingApi.code2Token
This end point is invoked by the FinTechApi to retrieve token used to send subsequent service requests to the TppBankingApi. We call this token PsuConsentSession.
  
### BankingService-040 : BankingProtocol.code2Token
Forward request to banking protocol.

### BankingService-050 : storePsuConsent
The returned PsuConsentSession is stored by the FinTechApi for future use. 

### BankingService-060 .. BankingService-067 Service Requests
The returned token is used to invoke the service request (ListTransactions). Service result is returned to the FinTechApi and displayed to the PSU.