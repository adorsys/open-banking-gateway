# Initiate Consent
General terms defined in the [dictionary](dictionary.md)

## Definitiion
Initiates a consent request. Generally the consent request is not explicitly initiated by the PSU. When the PSU requests for a banking service, if the FinTech has an existing consent that covers the service, no new consent will be initiated.

## Diagram
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/4-initiateAisConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases
### 1. FinTechUI displays BankProfile to PSU
==> FinTechUI --> psu : displayBankServices(BankProfile)
The result of a bank selection ist that the FinTechUI displays the list of services offered by the selected bank to the PSU.

### 2. PSU selects a service (Like listBankAccounts)
==> psu -> FinTechUI ++ : selectService\n"listBankAccounts(BankProfile)"

### 3. FinTechUI forwards service request to FinTechApi
==> FinTechUI -> FinTechApi ++ : listBankAccounts\[FinTechLoginSessionCookie,\nUserAgentContext\](BankProfile,ListBankAccountSpec)<>
- The attached [FinTechLoginSessionCookie](dictionary.md#FinTechLoginSessionCookie) is used to maintain session between PSU and FinTech.
- The attached [UserAgentContext](dictionary.md#UserAgentContext) describes details associated with the user agent of the PSU.
- The given [BankProfile](dictionary.md#BankProfile) contains meta information associated with the selected Bank.
- The ListBankAccountSpec specifies details of the service requested by the PSU.

#### 3.1 Load PsuConsentSession
==> FinTechApi -> FinTechApi : psuConsentSession\n(FinTechLoginSessionCookie,\nBankProfile,ListBankAccountSpec)<>
FinTechApi loads any matching existing PsuConsentSession. The FinTechLoginSessionCookie holds the reference of the PSU in the system of the FinTech. The PsuConsentSession contains the reference to any PSU consent stored in the realm of the TPP. If there is no consent suitable for the given operation, the PsuConsentSession will be empty.

### 4. FinTechApi forwards service request to TppBankingApi
==> FinTechApi -> TppBankingApi ++ : listBankAccounts\[UserAgentContext,\nPsuConsentSession,FinTechContext\]\n(BankProfile,ListBankAccountSpec)<>
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
#### 6.1 Initiating a Consent with the ASPSP
If there is no suiatable consent available, the BankingProtocol will first proceed with a consent initiation request.. This is, an initiated service request will either ends up in the expected service response or first redirect the PSU to the [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi).
Whether this operation is necessay or not depends on the [AspspBankingApi](dictionary.mdAspspBankingApi) interface. The selected banking protocol will know how to deal with this.
The Associated [TppContext](dictionary.md#TppContext) contains Tpp identifying information.

#### 6.2 ConsentInit Response
The response of the consent init request depends on the ASPSP implementation. It generally provides information needed to collect PSU identification information in the embedded case or information needed to redirect the PSU to the [OnlineBankingApi](dictionary.md#OnlineBankingApi).
The result of a consent init session also carries an [TppConsentSession](dictionary.md#TppConsentSession), containing all information needed to be stored by the Tpp for the reference of the started consent session.

#### 6.3 BankingProtocol calls [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) for a redirectCode

#### 6.4 [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi)
The [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) will encrypt and store the redirect session, indexing it with a redirectCode that can be used upon redirection by the ConsentAuthorisationApi to retrieve the corresponding [TppConsentSession](dictionary.md#TppConsentSession).
 
##### 6.4a Encryption
Encryption is performed to prevent unlawfull use of contained information in the Tpp's backend environment during the redirect session.

##### 6.4b Storage an Expiration
Encrypted [TppConsentSession](dictionary.md#TppConsentSession) shall only be stored for the duration of the redirect session.

##### 6.4c Auto Cleanup
Auto Cleanup process will make sure all expired redirect sessions are removed from that storage.

#### 6.5 [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi) returns redirectCode to BankingProtocol

#### 6.6 BankingProtocol reproduces [PsuConsentSession](dictionary.md#PsuConsentSession) from the [TppConsentSession](dictionary.md#TppConsentSession)

#### 6.7 Resulting Redirect Information is returned to the TppBankingApi
The attached [AspspRedirectInfo](dictionary.md#AspspRedirectInfo) contains all information necessary to redirect the PSU to the [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi).

#### 6.8 TppBankingApi returns the [PsuConsentSession](dictionary.md#PsuConsentSession) an a redirectCode to [FinTechApi](dictionary.md#FinTechApi)

#### 6.9 [FinTechApi](dictionary.md#FinTechApi) redirects userAgent to the [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi)
- [PsuUserAgent](dictionary.md#PsuUserAgent) redirection happens using a HTTP_302
- redirectCode is attached as a query parameter
- produced [FinTechLoginSessionCookie](dictionary.md#FinTechLoginSessionCookie) is returned as a cookie to the [PsuUserAgent](dictionary.md#PsuUserAgent)

### 7. Suitable Consent Present

#### 7.1 Forward Service Request to ASPSP
Service request ist forwarded to the [AspspBankingApi](dictionary.md#AspspBankingApi) together with a reference to the consent.
The Associated [TppContext](dictionary.md#TppContext) contains Tpp identifying information.

#### 7.2 Returned Service Response if sent and displayed to the PSU.
