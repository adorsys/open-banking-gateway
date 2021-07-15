# Dictionary

- [AisConsentSpec](dictionary.md#AisConsentSpec)
- [App](dictionary.md#App)
- [ASPSP](dictionary.md#ASPSP)
- [AspspBankingApi](dictionary.md#AspspBankingApi)
- [BankDescriptor](dictionary.md#BankDescriptor)
- [BankingProtocolSelector](dictionary.md#BankingProtocolSelector)
- [BankingProtocol](dictionary.md#BankingProtocol)
- [BankProfile](dictionary.md#BankProfile)
- [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi)
- [ConsentAuthorisationUI](dictionary.md#ConsentAuthorisationUI)
- [ConsentData](dictionary.md#ConsentData)
- [Dictionary](dictionary.md#Dictionary)
- [FinTech](dictionary.md#FinTech)
- [FinTechApi](dictionary.md#FinTechApi)
- [FinTechContext](dictionary.md#FinTechContext)
- [FinTechDC](dictionary.md#FinTechDC)
- [FinTechUI](dictionary.md#FinTechUI)
- [Information](dictionary.md#Information)
- [OnlineBanking2ConsentAuthRedirectInfoPage](dictionary.md#OnlineBanking2ConsentAuthRedirectInfoPage)
- [OnlineBankingApi](dictionary.md#OnlineBankingApi)
- [OnlineBankingConsentSessionCookie](dictionary.md#OnlineBankingConsentSessionCookie)
- [OnlineBankingLoginSessionCookie](dictionary.md#OnlineBankingLoginSessionCookie)
- [OnlineBankingUI](dictionary.md#OnlineBankingUI)
- [PSU](dictionary.md#PSU)
- [PsuConsentSession](dictionary.md#PsuConsentSession)
- [PsuIdentifier](dictionary.md#PsuIdentifier)
- [PsuUserAgent](dictionary.md#PsuUserAgent)
- [PsuUserDevice](dictionary.md#PsuUserDevice)
- [RedirectSession](dictionary.md#RedirectSession)
- [SessionCookie](dictionary.md#SessionCookie)
- [Sharing](dictionary.md#Sharing)
- [TppBankingApi](dictionary.md#TppBankingApi)
- [TppBankSearchApi](dictionary.md#TppBankSearchApi)
- [TppConsentSession](dictionary.md#TppConsentSession)
- [TppContext](dictionary.md#TppContext)
- [TPP](dictionary.md#TPP)
- [UserAgentContext](dictionary.md#UserAgentContext)
- [WebBrowser](dictionary.md#WebBrowser)

## <a name="ConsentData"></a> ConsentData    
In the context of Open Banking, a consent encompasses all information necessary to provide a third party with the authorization to access banking services on behalf of the PSU. These are:
- PSU banking identifier information known as (psuId, psuCorporateId)
- PSU account details information (like account numbers, iban, ...)
- PSU payment orders (including beneficiary names, amounts, ...)
- PSU authentication methods

All these information are stored in different devices during the consent authorization session. Form of storages are among others:
- Held in the browser page for display to the PSU
- Stored in the Cookie for reuse by the corresponding backend
- Stored in backend databases for transfer to other server components
- Stored in backend databases for reuse by server components.

For the purpose of protecting these data, framework is designed to always have consent data encrypted while at rest or on transit. General logic is that encrypted payload and encryption key do not reside in the same environment, unless need for decryption and processing of those data.

Following object hold consent data
- [TppConsentSession](dictionary.md#TppConsentSession)
- [RedirectSession](dictionary.md#RedirectSession)
- [PsuConsentSession](dictionary.md#PsuConsentSession)

## <a name="PsuUserDevice"></a> PsuUserDevice

A PSU user device runs applications used by the PSU to access banking functionality. Those applications are generally called PsuUgerAgents.

## <a name="PsuUserAgent"></a> PsuUserAgent

Application running on a PSU device and used by the PSU to access banking functionality. We are describing the two main types of PsuUserAgents.

### <a name="WebBrowser"></a>WebBrowser

A Web browser is considered compliant in the context of this framework when it can protect specific information used between the PusUserDevice and the the corresponding server application to track the user session. For session tracking, this framework uses [Cookies RFC6265](https://tools.ietf.org/html/rfc6265). 

#### Redirection and Data Sharing
We assume all three applications FinTechApi, ConsentAuthorisationApi, OnlineBankingApi are hosted on different domains. This is, we are not expecting Cookies set by one application to be visible to another application (this might still happen on some local development environment, where everything runs on localhost). 
We also do not advice adding persistent information to __RedirectUrl__, as these are log files everywhere on infrastructure components in data centers. __RedirectUrl__ shall instead carry __OneTime__ and __ShortLived__ authorization code we call __code__, that can be used to retrieved shared payload through an authenticated back channel connection. This is the practice borrowed from [oAuth2 RFC6749](https://tools.ietf.org/html/rfc6749). Following table shows defined redirects and corresponding back chanel endpoints.

| Origin Application | Redirecting Application | Response Code; Location ; AuthCodeParam; Expiration | Redirect Target Application | Destination Application  | Data EndPoint at Origin Application |
| -- | -- | -- | -- | -- | -- |
| TppBankingApi | FinTechApi | 302 ; /auth ; code ; 5s | ConsentAuthorisationApi | ConsentAuthorisationApi | /loadTppConsentSession |
| ConsentAuthorisationApi | ConsentAuthorisationApi | Proprietary banking API. Assume RFC6749. /auth | OnlineBankingApi | OnlineBankingApi | none |
| OnlineBankingApi | OnlineBankingApi | 302 ; \[/ok\|/nok\] ; code ; 5s | ConsentAuthorisationApi | ConsentAuthorisationApi | /token |
| ConsentAuthorisationApi | ConsentAuthorisationApi | 302 ; \[/ok\|/nok\] ; code ; 5s | FinTechApi | TppBankingApi | /loadTppConsentSession |

#### Keeping Session Information
We assume all three applications FinTechApi, ConsentAuthorisationApi, OnlineBankingApi maintain their own session information. This framework uses following terms to name the session information held by an application on the UserAgent of the PSU.

| Application | SessionCookie |
|--|--|
| FinTechApi | Psu2FintechLoginSession |
| ConsentAuthorisationApi | ConsentAuthSessionCookie |
| OnlineBankingApi | OnlineBankingConsentSessionCookie |

Session information can also be kept across redirect life cycles. Upon redirecting the UserAgent to another application, the redirecting application can set Cookies that will be resent to the domain with future requests. This way, there will be no need to maintain user session information in temporary databases on the server, thus keeping server tiny.    

### <a name="App"></a>Native App
The UserAgent might be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a user device: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636) 
For the purpose of kepping the overall architecture of this framework simple, we will require native applications to provide the same behavior as the WebBrowser described above.

### <a name="UserAgentContext"></a> UserAgentContext
Independent on the type of PsuUgerAgent, OpenBanking interfaces will require transmission of a class of information associated with the PsuUserAgent so they can perform verification of the authenticity of the original PSU request and customize the response produced for intermediary layers. We group these data under the name "UserAgentContext". Following header names account among the UserAgentContext: IP-Address, IP-Port, Accept, Accept-Charset, Accept-Encoding, Accept-Language, Device-ID, User-Agent, Geo-Location, Http-Method.

### <a name="FinTechUI"></a> FinTechUI
UI Application running on the PsuUserAgent and used by the PSU to access the FinTechApi

### <a name="ConsentAuthorisationUI"></a> ConsentAuthorisationUI
UI used by PSU to authoraise consent in embedded case.

### <a name="OnlineBankingUI"></a> OnlineBankingUI
This UI manages the interaction between the PSU and the ASPSP in redirect cases.

## <a name="FinTech"></a> FinTech
Organisation that uses Online Banking Services provided by TPP to service PSU with additional services.
FinTech may or may not have own TPP License.

## <a name="FinTechDC"></a> FinTechDC
Data center environment of the FinTech. Host the FinTechApi.

### <a name="FinTechApi"></a> FinTechApi
Financial web service provided by the FinTech.

## <a name="TppDC"></a> Tpp Data Center
Data center environment of the TPP

### <a name="TppBankingApi"></a> TppBankingApi
Tpp backend providing access to ASPSP banking functionality. This interface is not directly accessed by the PSU but by the FinTechApi. FinTechApi will use a FinTechContext to authenticate with the TppBankingApi.

### <a name="TppBankSearchApi"></a> TppBankSearchApi
Repository of banks maintained in the TPP's open banking gateway. The banking search API will later present an interface to configure profiles attached to listed banks.

### <a name="BankDescriptor"></a> BankDescriptor
Descriptive information assocaited with a bank like:
- The name of the Bank
- The address of the bank
- The bank identification code

### <a name="BankProfile"></a> BankProfile
BankingApi profile information associated with a bank like:
- The BankingProtocol used to connect with the bank
- List of Banking services provided by the BankingApi of the bank
- SCA approcahes associated with the BankingApi
- ScaUIMetadaData: Screens and field used to collect user authentication data.
- Actions to be performed by the PSU prior to using the BankingProtocol

#### <a name="AisConsentSpec"></a> AisConsentSpec
Specification associated with an AisConsent. This is highly dependent on the BankProfile. Following information might be carried by an AisConsentSpec object:
- recurringIndicator
- validUntil
- frequencyPerDay
- combinedService
- accountAccessTemplate
- availableAccounts[availableAccountsWithBalances, allAccounts]
- allPsd2[allAccounts]

### <a name="FinTechContext"></a> FinTechContext
Information used to identify the FinTech application at the TppBankingApi. For example a FinTech SSL client certificate or an APIKey or an oAuth2 Password Grant Credential.

### <a name="PSU"></a> PSU

A Payment Services User is a natural or legal person making use of a payment service as a payee, payer or both.

### <a name="PsuConsentSession"></a> PsuConsentSession
Information associated with the consent as exchanged between the FinTechApi and the TppBankingApi. Generally contains:
- Data needed to customize psu access at the ConsentAuthorisationApi (showInfoPanel, fintechStateHash)
- Data needed to manage redirection of PSU from the TppConsentSession to the FintechUI like (FinTech-Redirect-URI, FinTech-Nok-Redirect-URI, FinTech-Explicit-Authorisation-Preferred, FinTech-Content-Negotiation)

Object also contains information associated with the PSU requesting service if available.
- The identifier of the PSU in the realm of the Tpp PsuIdentifier
- Existing Consent References if any.

#### <a name="PsuIdentifier"></a> PsuIdentifier
This is the identifier of the PSU in the FinTech2Tpp relationship. This identifier can be saved once a consent has been successfully established to allow for reuse of existing consent in future sessions.

### <a name="ConsentAuthorisationApi"></a> ConsentAuthorisationApi 
Interface used by the PSU to authorize a consent.

### <a name="SessionCookie"></a> SessionCookie and XSRF
We assume all three applications FinTeApi, ConsentAuthorisationApi, OnlineBakingApi maintain their own session information with corresponding UIs. We assume those APIs use cookies to maintain session with the corresponding user agents. In the context of this framework, those cookies are called SessionCookies. We also expect a following behavior from APIs and UserAgents:

* A response that sets a SessionCookie also carries a corresponding X-XSRF-TOKEN in the response header.
* A request that authenticates with a session cookie must also add the X-XSRF-TOKEN to the request header.

### <a name="RedirectSession"> RedirectSession
Holds consent information for the duration of a redirect. Redirect patterns are described [below](dictionary.md#Redirection).

### <a name="BankingProtocol"></a> BankingProtocol
Component managing access to a banking interface initiative. WE will have to deal with many protocols like NextGenPSD2, HBCI, OpenBanking UK, PolishAPI.

### <a name="BankingProtocolSelector"></a> BankingProtocolSelector
Help select a banking protocol.

## <a name="AspspDC"></a> Aspsp Data Center
Data center environment of the ASPSP

## <a name="ASPSP"></a> ASPSP
Account Servicing Payment Service Providers provide and maintain a payment account for a payer as defined by the PSRs and, in the context of the Open Banking Ecosystem are entities that publish Read/Write APIs to permit, with customer consent, payments initiated by third party providers and/or make their customers’ account transaction data available to third party providers via their API end points.

### <a name="AspspBankingApi"></a> AspspBankingApi 
Api banking provided by ASPSP. This interface is not directly accessed by the PSU but by the TppBankingApi. TppBankingApi will use a TppContext to authenticate with the TppBankingApi.

### <a name="TppContext"></a> TppContext
Information used to identify the Tpp application in the ASPSP environment. Like a TPP QWAC certificate.

### <a name="TppConsentSession"></a> TppConsentSession
Storage for consent data in the realm of the BankingProtocol. The banking protocol is both accessible to the TppBankingApi and the ConsentAuthorizationApi.

The cryptographic key needed to recover the TppConsentSession is always delivered by the calling layer. These are:
- FinTechUI -> FinTechApi -> TppBankingApi -> BankingProtocol : in this case the key needed to recover the TppConsentSession in contained in the PsuConsentSession. Generally that key will transitively originate from an interaction with the user agent. 
- CosentAuthorizationUI -> CosentAuthorizationApi -> BankingProtocol : in this case the key needed to recover the TppConsentSession originate from the ConsentAuthSessionCookie.

Beside consent data, additional data might be held in the TppConsentSession: 
- [FinTechContext](dictionary.md#FinTechContext): Data needed to authorize the FinTechApi (FinTechSSLCertificate, ApiKey, SignedJWT)
- Additional information needed for interaction between TPP and ASPSP but without any concern to the PSU.

### <a name="OnlineBankingApi"></a> OnlineBankingApi
Generally the online banking application on an ASPSP. In redirect cases, the ASPSP OnlineBankingApi establishes a direct session with the PSU to allow the PSU to identify himself, review and authorize the consent. 

### <a name="OnlineBankingConsentSessionCookie"></a> OnlineBankingConsentSessionCookie
This is a Cookie used to maintain the session between the OnlineBankingUI and the OnlineBankingApi. As a recommendation, the validity of this Cookie shall be limited to the life span of the consent session. As the OnlineBankingApi redirects the PSU back to the ConsentAuthorisationApi up on completion of the consent session. Redirection happens independently on whether the consent was authorized or not.
 
### <a name="OnlineBanking2ConsentAuthRedirectInfoPage"></a> OnlineBanking2ConsentAuthRedirectInfoPage
It is recommended to inform the PSU prior to redirecting the PSU back to the TPP. This UI-Panel will be called OnlineBanking2ConsentAuthRedirectInfoPage. If the ASPSP is using a trusted environment (Native App) and wants to keep the relationship to the PSU alive, it is necessary to store this relationship in a separated OnlineBankingLoginSessionCookie.

### <a name="OnlineBankingLoginSessionCookie"></a> OnlineBankingLoginSessionCookie
This Cookie will be used by the ASPSP to keep a login session of the PSU over the life span of consent session. This will prevent the PSU from performing the login step for upcoming consent sessions.


## <a name="TPP"></a> TPP
A TPP is a Third Party Provider - a legal entity that holds a TPP License provided by NCA (PISP, AISP etc). and operates with corresponding QWAC Certificate.
TPP may serve FinTech companies with XS2A Services.
