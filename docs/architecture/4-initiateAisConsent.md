# List Of Transactions
General terms defined in the [dictionary](dictionary.md)

## Definition
Request the list of transactions for a given bank account. Initiates a consent request if necessary. Generally the consent request is not explicitly initiated by the PSU. When the PSU requests for a banking service, if the FinTech has an existing consent that covers the service, no new consent will be initiated.

## Diagram
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/4-initiateAisConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases
### 1. FinTechUI displays BankProfile to PSU
==> FinTechUI --> psu : displayBankServices(BankProfile)
The result of a bank selection ist that the FinTechUI displays the list of services offered by the selected bank to the PSU.

### 2. PSU selects a service (Like listTransactions)
==> psu -> FinTechUI ++ : selectService\n"listTransactions(BankProfile)"

### 3. FinTechUI forwards service request to FinTechApi
==> FinTechUI -> FinTechApi ++ : listTransactions\[FinTechLoginSessionCookie,\nUserAgentContext\](BankProfile,ListTransactionsSpec)<>
- The attached [FinTechLoginSessionCookie](dictionary.md#FinTechLoginSessionCookie) is used to maintain session between PSU and FinTech.
- The attached [UserAgentContext](dictionary.md#UserAgentContext) describes details associated with the user agent of the PSU.
- The given [BankProfile](dictionary.md#BankProfile) contains meta information associated with the selected Bank.
- The ListTransactionsSpec specifies details of the service requested by the PSU.

#### 3.1 Load PsuConsentSession
==> FinTechApi -> FinTechApi : psuConsentSession\n(FinTechLoginSessionCookie,\nBankProfile,ListTransactionsSpec)<>
FinTechApi loads any matching existing [PsuConsentSession](dictionary.md#PsuConsentSession). The FinTechLoginSessionCookie holds the reference of the PSU in the system of the FinTech.

### 4. FinTechApi forwards service request to TppBankingApi
==> FinTechApi -> TppBankingApi ++ : listTransactions\[UserAgentContext,\nPsuConsentSession,FinTechContext\]\n(BankProfile,ListTransactionsSpec)<>
The associated [FinTechContext](dictionary.md#FinTechContext) contains identification information associated with the FinTech.

#### 4.1 Loads the BankingProtocol from the given BankProfile
TppBankingApi selects the [BankingProtocol](dictionary.md#BankingProtocol) based on the given BankProfile.

### 5. TppBankingApi forwards service request to BankingProtocol
The [BankingProtocol](dictionary.md#BankingProtocol) associated with the given BankProfile decides on how to proceed with the request. 
BankingProtocol can:

#### 5.1 Load TppConsentSession
- Use an eventual consentId contained in the given [PsuConsentSession](dictionary.md#PsuConsentSession) to load an existing [TppConsentSession](dictionary.md#TppConsentSession). 
- Use the loaded [TppConsentSession](dictionary.md#TppConsentSession) to retrieve an existing consent and proceed to the ASPSP with the service request.

### 6. No Suitable Consent Present
#### 6.0 Initiating a Consent with the ASPSP
If there is no suitable consent available, the BankingProtocol will first proceed with a consent initiation request.. This is, an initiated service request will either ends up in the expected service response or first redirect the PSU to the [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi).
Whether this operation is necessary or not depends on the [AspspBankingApi](dictionary.mdAspspBankingApi) interface. The selected banking protocol will know how to deal with this.
The Associated [TppContext](dictionary.md#TppContext) contains Tpp identifying information.

#### 6.1 ConsentInit Response
The response of the consent init request depends on the ASPSP implementation. It generally provides information needed to collect PSU identification information in the embedded case or information needed to redirect the PSU to the [OnlineBankingApi](dictionary.md#OnlineBankingApi).
The result of a consent init session also carries an [TppConsentSession](dictionary.md#TppConsentSession), containing all information needed to be stored by the Tpp for the reference of the started consent session.

#### 6.2 BankingProtocol calls RedirectSessionStoreApi for a redirectCode

#### 6.3 RedirectSessionStoreApi
The [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) will encrypt and store the redirect session, indexing it with a redirectCode that can be used upon redirection by the ConsentAuthorisationApi to retrieve the corresponding [TppConsentSession](dictionary.md#TppConsentSession).
 
##### 6.3a Encryption
Encryption is performed to prevent unlawfull use of contained information in the Tpp's backend environment during the redirect session.

##### 6.3b Storage an Expiration
Encrypted [TppConsentSession](dictionary.md#TppConsentSession) shall only be stored for the duration of the redirect session.

##### 6.3c Auto Cleanup
Auto Cleanup process will make sure all expired redirect sessions are removed from that storage.

#### 6.4 RedirectSessionStoreApi returns redirectCode to BankingProtocol
The [redirectCode](dictionary.md#redirectCode) is a one time string that contains information used to retrieve redirectInfo from the TPP Server in a back channel.
The redirectCode is short lived (like 10 seconds). This is, TPP server does not need to hold the record indexed by this redirectCode for more than the given expiration time. Record must also be deleted by the TPP on first retrieval by the ConsentAPI.

#### 6.5 BankingProtocol reproduces PsuConsentSession from the TppConsentSession

#### 6.6 Resulting Redirect Information is returned to the TppBankingApi
The attached [AspspRedirectInfo](dictionary.md#AspspRedirectInfo) contains all information necessary to redirect the PSU to the [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi).

#### 6.7 TppBankingApi returns the PsuConsentSession an a redirectCode to FinTechApi

#### 6.8 FinTechConsentSessionCookie
Available in the request header. This cookie shall be set for the Max time given to the PSU for the authorization of the corresponding consent. The cookie can be bound to the end point FinTechApi.consentAuthDone so it does no need to be transported to the server on other requests. 

#### 6.8a finTechConsentSessionState
Will be used to read and validate the corresponding FinTechConsentSessionCookie.

#### 6.9 FinTechApi redirects userAgent to the ConsentAuthorisationApi
- [PsuUserAgent](dictionary.md#PsuUserAgent) redirection happens using a HTTP_302
- redirectCode is attached as a query parameter
- produced [FinTechLoginSessionCookie](dictionary.md#FinTechLoginSessionCookie) is returned as a cookie to the [PsuUserAgent](dictionary.md#PsuUserAgent)

### 7. Suitable Consent Present

#### 7.1 Forward Service Request to ASPSP
Service request is forwarded to the [AspspBankingApi](dictionary.md#AspspBankingApi) together with a reference to the consent.
The Associated [TppContext](dictionary.md#TppContext) contains Tpp identifying information.

#### 7.2 Returned Service Response if sent and displayed to the PSU.

