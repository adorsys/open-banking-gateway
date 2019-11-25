# Initiate Consent
General terms defined in the [dictionary](dictionary.md)

## Definitiion
Initiates a consent request. Generally the consent request is not explicitly initiated by the PSU. When the PSU request for a banking service, if the FinTech has an existing consent that covers the service, no consent will be initiated.

## Diagram 

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/4-initiateAisConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### 1. FinTechUI displays BankProfile to PSU
### 2. PSU selects a service (Like listBankAccounts)
### 3. FinTechUI forwards service request to FinTechApi
- The attached Psu2FinTechLoginSessionCookie is used to maintain session between PSU and FinTech.
- The attached UserAgentContext describes details associated with the user agent of the PSU.
- The given BankProfile contains meta information associated with the selected Bank.
- The ServiceSpec specifies/details the service request of the PSU.
### 4. FinTechApi forwards service request to TppBankingApi
++ : listBankAccounts[UserAgentContext,\nFinTech2TppConsentSession]\n(BankProfile,AisConsentSpec)<>
### 5. Creates TppConsentSession from FinTech2TppConsentSession
### 6. Loads the BankingProtocol from the given BankProfile
### 7. TppBankingApi forwards service request to BankingProtocol
The [BankingProtocol](dictionary.md#BankingProtocol) associated with the given BankProfile decides on how to proceed with the request. 
BankingProtocol can:
- Use the consentId contained in the attached [TppConsentSession](dictionary.md#TppConsentSession) to retrieve an existing consent and proceed to the ASPSP with the service request.
- If there is no suiatable consent available, the BankingProtocol will first proceed with a consent initiation request.
This is, an initiated service request will either end up in the expected service response or first redirect the PSU to the [TppConsentSessionApi](dictionary.md#TppConsentSessionApi)
### 8. Initiating a Consent with the ASPSP
Whether this operation is necessay or not depends on the [AspspBankingApi](dictionary.mdAspspBankingApi) interface. The selected banking protocol will know how to deal with this.
### 9. ConsentInit Response
The response of the consent init request depends on the ASPSP implementation. It generally provides information needed to collect PSU identification inforation in the embedded case or information needed to redirect the PSU to the [AspspConsentSessionApi](dictionary.md#AspspConsentSessionApi).
The result of a consent init session also carries an [Tpp2AspspConsentSession](dictionary.md#Tpp2AspspConsentSession), containing all information needed to be stored by the Tpp for the reference of the started consent session.
### 10. BankingProtocol transforms [Tpp2AspspConsentSession](dictionary.md#Tpp2AspspConsentSession) into a [TppConsentSession](dictionary.md#TppConsentSession)
The ASPSP consent session might also be stored at tpp site, if all information can not the encoded into the reference returned to the fintehct.
### 11. Resulting [TppConsentSession](dictionary.md#TppConsentSession) is returned to the TppBankingApi
### 12. TppBankingApi calls [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) for a redirectCode
### 13. [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi)
The [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) will encrypt and store the redirect session, indexing it with a redirectCode that can be used upon redirection by the TppConsentSessionApi to retrieve the corresponding [TppConsentSession](dictionary.md#TppConsentSession). 
#### 13a. Encryption
Encryption is performed to prevent unlawfull use of contained information in the Tpp's backend environment during the redirect session.
#### 13b. Storage an Expiration
Encrypted [TppConsentSession](dictionary.md#TppConsentSession) shall only be stored for the duration of the redirect session.
#### 13c. Auto Cleanup
Auto Cleanup process will make sure all expired redirect sessions are removed from that storage.
### 14. [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) returns redirectCode to TppBankingApi
### 15. TppBankingApi reproduces [FinTech2TppConsentSession](dictionary.md#FinTech2TppConsentSession) from the [TppConsentSession](dictionary.md#TppConsentSession)
### 16. TppBankingApi returns the [FinTech2TppConsentSession](dictionary.md#FinTech2TppConsentSession) an a redirectCode to [FinTechApi](dictionary.md#FinTechApi)
### 17. [FinTechApi](dictionary.md#FinTechApi) redirects userAgent to the [TppConsentSessionApi](dictionary.md#TppConsentSessionApi)
- [PsuUserAgent](dictionary.md#PsuUserAgent) redirection happens using a HTTP_302
- redirectCode is attached as a query parameter
- produced [Psu2FintechLoginSessionCookie](dictionary.md#Psu2FintechLoginSessionCookie) is returned as a cookie to the [PsuUserAgent](dictionary.md#PsuUserAgent)