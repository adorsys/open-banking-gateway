# Initiate AisConsent

## Diagram
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/5-redirectPsuToConsentAPI.puml&fmt=svg&vvv=1&sanitize=true)  

### InitConsent-010 [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi)
The redirect starts with a get request to ConsentAuthorisationApi.auth. The entryPoint of the ConsentAuthorisationApi for processing a consent initiated on the TppBankingApi side.

### InitConsent-020 BankingProtocolFacade.redirectSession(redirectCode)
The ConsentAuthorisationApi forwards the redirect call to the BankingProtocolFacade.

### InitConsent-030 BankingProtocolFacade.selectBankingProtocol(redirectCode)
BankingProtocolFacade will use the given redirectCode to load the matching BankingProtocol. This means that the protocol selection information must be encoded in the redirectCode. See [Issue #54](https://github.com/adorsys/open-banking-gateway/issues/54).

### InitConsent-040 BankingProtocol.redirectSession(redirectCode)
The BankingProtocolFacade forwards the redirect call to the BankingProtocol.

### InitConsent-050 BankingProtocol.loadAuthSession(redirectCode)
BankingProtocol uses the redirectCode to retrieve the AuthSession.

### PreAuthentication Required
Some OpenBanking protocols required the PsuId to be provided with the very initial consent initiation request. In some cases, the OpenBanking protocol will specify a way to collect the PSU identity. This is the approach taken by the AuthPreStep as defined by the NextGenPSD2 protocol. In some other cases, the OpenBanking will not define any way of collecting this PSU identity. In such a case, the PsuIdentity will be collected using a UI defined by the TPP.
Depending on which interface is taken to collect the PsuIdentity, we will have to mark if this collected identity is authenticated or not. If the identity collection interface allow the provision of a password and verifycation of the association between the given identity and the password, we call this an authenticated identity. If the interface only provide a collection of the PsuIdentity without proof of ownership of this identity, will call this an unauthenticated identity.

### InitConsent-060 BankingProtocol:ThrowIdentifyPSU
- Inputs: (protocolId, seviceSessionId, serviceSessionKey,authId)
If the protocol requires a PsuIdentity and does not specify an identity provider (e.g.: oAuth2) used to collect that identity, we assume the BankingProtocol is requesting the ConsentAuthorisationApi to collect the identity unauthenticated. In this case, the BankingProtocol will throw an ThrowIdentifyPSU exception to instruct the BankingProtocolFacade to collect the identity of the PSU. The thrown exception carries following parameters:
- The protocolId: The protocolId contains information necessary to rediscover the banking protocol.
- The seviceSessionId and the serviceSessionKey : contains information later used by the BankingProtocol to laod the ServiceSession.
- The authId: references this authorization instance in the scope of a service session, as a service request can involve more that one authorization session.

### InitConsent-061 BankingProtocolFacade:ThrowIdentifyPSU
- Inputs: (protocolId, seviceSessionId, serviceSessionKey,authId)
The BankingProtocolFacade instructs the ConsentAuthorisationApi to collect the identity of the PSU. 

### InitConsent-062 ConsentAuthorisationApi.createSessionCookie
- Inputs: (protocolId,seviceSessionId, serviceSessionKey,authId)
- Returns: SessionCookie,state
The ConsentAuthorisationApi creates a SessionCookie containing information provided in the call, generates a state parameter to protect the SessionCookie and redirects the call to the psuIdScreen of the ConsentAuthorisationUI.

### InitConsent-063 ConsentAuthorisationUI.redirect302
- Specification: redirect302[SessionCookie,ConsentAuthorisationUI.psuIdScreen()<q:state>
The ConsentAuthorisationApi instructs the PsuUserDevice to redirect the PSU to the psuIdScreen of the ConsentAuthorisationUI.

### InitConsent-064 ConsentAuthorisationUI:displayPsuIdScreen
The ConsentAuthorisationUI displays the psuIdScreen to the PSU.

### InitConsent-065 ConsentAuthorisationUI.enterPsuId
The PSU enters the aspsp's PsuId in the filed provided by the ConsentAuthorisationUI.

### InitConsent-066 ConsentAuthorisationApi.updatePsuIdentification
- Spec: updatePsuIdentification[SessionCookie, X-XSRF-TOKEN](psu-id@tpp,psu-id@aspsp)
The ConsentAuthorisationUI sends the the collected data to the ConsentAuthorisationApi.

### InitConsent-067 ConsentAuthorisationApi.validateSessionCookie
- Inputs: (SessionCookie,xsrfToken)
- Results: protocolId,seviceSessionId,serviceSessionKey,authId
The ConsentAuthorisationApi uses the provided xsrfToken to validate authenticity of the SessionCookie and the extract processing information out of the cookie.

### InitConsent-068 BankingProtocolFacade.updatePsuIdentification
- Spec: updatePsuIdentification(psu-id@tpp,psu-id@aspsp,protocolId,seviceSessionId,serviceSessionKey,authId)
The ConsentAuthorisationApi forwards the call to the BankingProtocolFacade.

### InitConsent-069 BankingProtocolFacade.selectBankingProtocol
- Spec: selectBankingProtocol(protocolId):BankingProtocol
The BankingProtocolFacade uses the provided protocolId to select the target BankingProtocol.

### InitConsent-070 BankingProtocol.updatePsuIdentification
- Spec: updatePsuIdentification(psu-id@tpp,psu-id@aspsp,seviceSessionId,serviceSessionKey,authId)
The BankingProtocolFacade forwards the call to the BankingProtocol.


### InitConsent-080 BankingProtocol:ThrowAuthenticatePSU
- Inputs: (protocolId,seviceSessionId, serviceSessionKey,authId, idpUrl)
If the protocol requires a PsuIdentity and do specify an identity provider (e.g.: oAuth2) used to collect that identity, we assume the idp is going to authenticate the PSU and therefore, the BankingProtocol will throw an ThrowAuthenticatePSU exception to instruct the BankingProtocolFacade to send the PSU to the specified Idp. The thrown exception carries following parameters:
- The protocolId: The protocolId contains information necessary to re-discover the banking protocol.
- The seviceSessionId and the serviceSessionKey : contains information later used by the BankingProtocol to laod the ServiceSession.
- The authId: references this authorization instance in the scope of a service session, as a service request can involve more that one authorization session.
- The idpUrl the url of the idp used to collect the psu identity on behalf of the ASPSP.

### InitConsent-081 BankingProtocolFacade:ThrowAuthenticatePSU
- Inputs: (protocolId,seviceSessionId, serviceSessionKey,authId,idpUrl)
The BankingProtocolFacade instructs the ConsentAuthorisationApi to collect the identity of the PSU. 

### InitConsent-082 ConsentAuthorisationApi.createSessionCookie
- Inputs: (protocolId,seviceSessionId, serviceSessionKey,authId,idpUrl)
- Returns: SessionCookie,state
The ConsentAuthorisationApi creates a SessionCookie containing information provided in the call, generates a state parameter to protect the SessionCookie and redirects the call to the redirectInfoPage of the ConsentAuthorisationUI.

### InitConsent-083 ConsentAuthorisationUI.redirect302
- Specification: redirect302[SessionCookie,ConsentAuthorisationUI.psuIdScreen()<q:state>
The ConsentAuthorisationApi instructs the PsuUserDevice to redirect the PSU to the redirectInfoPage of the ConsentAuthorisationUI.

### InitConsent-084 ConsentAuthorisationUI:displayRedirectInfoPage
The ConsentAuthorisationUI displays the redirectInfoPage to the PSU.

### InitConsent-085 ConsentAuthorisationUI.confirmRedirect
The PSU confirms the redirect to the idp of the ASPSP.

### InitConsent-086 ConsentAuthorisationApi.confirmRedirect
- Spec: confirmRedirect[SessionCookie, X-XSRF-TOKEN](psu-id@tpp,psu-id@aspsp)
The ConsentAuthorisationUI sends a redirect confirmation to the ConsentAuthorisationApi.

### InitConsent-087 ConsentAuthorisationApi.validateSessionCookie
- Inputs: (SessionCookie,xsrfToken)
- Results: protocolId,seviceSessionId,serviceSessionKey,authId,idpUrl
The ConsentAuthorisationApi uses the provided xsrfToken to validate authenticity of the SessionCookie and the extract processing information out of the cookie.

### InitConsent-088 ConsentAuthorisationApi.createRedirectCookie
- Inputs: (protocolId,seviceSessionId,serviceSessionKey,authId,idpUrl)
- Results: RedirectCookie,state
The ConsentAuthorisationApi creates a RedirectCookie containing those processing information and following specification:
- path: ConsentAuthorisationUI.fromIdpAuth<p:authId>
- exp: expected time needed by the PSU to authenticate with the idp of the ASPSP
- redirect-uri: ConsentAuthorisationUI.fromIdpAuth<p:authId>. This the uri that is going to be used by the idp to redirect the PSU back to the TPP.
- state: this is the state used to verify authenticity of the RedirectCookie. 

### InitConsent-089 ConsentAuthorisationApi:redirect302
- Spec: redirect302[RedirectCookie,\nOnlineBankingApi.redirectEntryPoint]\n()<redirect-uri, state>
The ConsentAuthorisationApi instructs the ConsentAuthorisationUI to redirect the PSU to the idp of the ASPSP.

### InitConsent-090 BankingProtocolFacade.fromIdpAuth
- Spec: fromIdpAuth[UserAgentContext,\nRedirectCookie]()\n<p:authId,q:code,q:state>
With this call, the idp of the APSPS redirects the PsuUserDevice to the ConsentAuthorisationUI of the TPP. This call will 

### InitConsent-091 ConsentAuthorisationApi.validateRedirectCookie
- Inputs: (RedirectCookie,state)
- Results: protocolId,seviceSessionId,serviceSessionKey,authId,idpUrl
The ConsentAuthorisationApi uses the provided state parameter to validate authenticity of the RedirectCookie and then extract processing information out of the cookie.

### InitConsent-092 BankingProtocolFacade.code2Token
- Inputs: (code,protocolId,seviceSessionId,serviceSessionKey,authId,idpUrl)
The ConsentAuthorisationApi forwards the call to the BankingProtocolFacade.

### InitConsent-093 BankingProtocolFacade.selectBankingProtocol
- Spec: selectBankingProtocol(protocolId):BankingProtocol
The BankingProtocolFacade uses the provided protocolId to select the target BankingProtocol.

### InitConsent-094 BankingProtocol.code2Token
- Spec: code2Token(code,seviceSessionId,serviceSessionKey,authId,idpUrl)
The BankingProtocolFacade forwards the call to the target BankingProtocol.

### InitConsent-095 OnlineBankingApi.code2Token
- Spec: GET:code2Token[\nTppContext]()<code>
The BankingProtocol calls the token endpoint of the idp of the ASPSP to request the token matching the returned authorization code.

### InitConsent-096 OnlineBankingApi:token
The idp of the ASPSP returns a token to the banking protocol. 

### InitConsent-096 BankingProtocol.psuId
the banking protocol extracts the needed psu-id from the returned token. 

### InitConsent-100 AspspBankingApi.initiateConsent
- Spec: initiateConsent[UserAgentContext,\nTppContext,psu-id@aspsp](AisConsent) 
The BankingProtocol sends an initiate consent request to the OpenBanking interface of the ASPSP (aka AspspBankingApi). 

### InitConsent-110 AspspBankingApi:200_OK
- Spec: 200_OK(AisConsent,\nAspspRedirectInfo,\nAspspChallenges)
The OpenBanking interface of the ASPSP returns information necessary to start the consent authorization process.
- AisConsent : a reference to the started ASPSP consent session
- AspspRedirectInfo: Information neede in case the TPP has to redirect the PSU to the OnlineBankingApi of the ASPSP
- AspspChallenges: Challenges and SCA meta information in case the TPP will have to collect PSU credentials at the ConsentAuthorisationApi.

### InitConsent-120 BankingProtocol.addToServiceSession
- Spec: addToServiceSession(AisConsent,\nAspspRedirectInfo,\nAspspChallenges)
The BankingProtocol stores the response of the call in the ServiceSession that also includes information like: protocolId, seviceSessionId, serviceSessionKey, authId.

### InitConsent-130 BankingProtocol:ServiceSession
The BankingProtocol also returns the ServiceSession containing information necessary to start the consent authorization process.

### InitConsent-140 BankingProtocolFacade:ServiceSession
The BankingProtocolFacade returns the ServiceSession containing information necessary to start the consent authorization process.

### InitConsent-150 ConsentAuthorisationApi.prepareResponse
-Specs: prepareResponse(ServiceSession):xsrfToken,AuthorizeResponse,SessionCookie
The ConsentAuthorisationApi prepares the response, producing:
- AuthorizeResponse : that will be sent back to the ConsentAuthorisationUI and used to manage interaction with the PSU and the ConsentAuthorisationUI.
- SessionCookie : that will be used to authenticate the interaction between the ConsentAuthorisationUI and the ConsentAuthorisationApi
- xsrfToken : used to authenticate SessionCookie 


### InitConsent-160 ConsentAuthorisationUI.displayBankLoginPage
- Spec: displayBankLoginPage[SessionCookie,X-XSRF-Token](AuthorizeResponse)
If the AuthorizeResponse indicates that the consent authorization is going to happen at the interface of the TPP (ConsentAuthorisationApi), the initial screen for the processing of challenges will be displayed.

### InitConsent-170 ConsentAuthorisationUI.displayRedirectInfoPage
- Spec: displayRedirectInfoPage[SessionCookie,X-XSRF-Token](AuthorizeResponse)
If the AuthorizeResponse indicates that the consent authorization process is going to happen at the interface of the ASPSP, the redirect info page will be displayed to the PSU, waiting for a confirmation to be redirected to the ASPSP online banking interface.
 
