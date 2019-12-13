# Dictionary

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

### WebBrowser

A Web browser is considered compliant in the context of this framework when it can protect specific information used between the PusUserDevice and the the corresponding server application to track the user session. For session tracking, this framework uses [Cookies RFC6265](https://tools.ietf.org/html/rfc6265). 

#### Security Considerations
The use of cookies provides the most elaborated way to protect a session established between a WebBrowser and server application. We assume a WebBrowser storing a cookie fulfills following requirements:
- Cookies carrying the attribute __HttpOnly__ are not provided access to scripts run by the UserAgent.
- Cookies carrying the attribute __Secure__ are only resent to the server over SSL connections.
- Expired Cookies (attribute __Expires__) are not resent to the server.
- Cookies shall never be transmitted to a domain not matching it's origin.

#### <a name="Redirection"></a> Redirection 
The server can request the WebBrowser to redirect the user to another page by returning a 30\[X\] response code to the WebBrowser. Redirection will generally happens in the same Browser environment. We will be using redirection to switch the user context from one application to another one. Following redirection will generally be found in this framework:
- FinTechApi to-> ConsentAuthorisationApi
- ConsentAuthorisationApi to-> OnlineBankingApi
- OnlineBankingApi backTo-> ConsentAuthorisationApi
- ConsentAuthorisationApi backTo-> FinTechApi

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

### Native App
The UserAgent might be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a user device: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636) 
For the purpose of kepping the overall architecture of this framework simple, we will require native applications to provide the same behavior as the WebBrowser described above.

### <a name="UserAgentContext"></a> UserAgentContext
All information associated with the PsuUserAgent. Like PSU-IP-Address, PSU-IP-Port, PSU-Accept, PSU-Accept-Charset, PSU-Accept-Encoding, PSU-Accept-Language, PSU-Device-ID, PSU-User-Agent, PSU-Geo-Location, PSU-Http-Method. Many backend API will require provisioning of the UserAgentContext to perform verification of the authenticity of the original PSU request and to customize the response produced for intermediary layers.

### <a name="FinTechUI"></a> FinTechUI
UI Application running on the PsuUserAgent and used by the PSU to access the FinTechApi

### <a name="ConsentAuthorisationUI"></a> ConsentAuthorisationUI
UI used by PSU to authoraise consent in embedded case.

### <a name="OnlineBankingUI"></a> OnlineBankingUI
This UI manages the interaction between the PSU and the ASPSP in redirect cases.

## <a name="FinTechDC"></a> FinTechDC
Data center environment of the FinTech. Host the FinTechApi.

### <a name="FinTechApi"></a> FinTechApi
Financial web service provided by the FinTech.

### <a name="FinTechLoginSessionCookie"></a> FinTechLoginSessionCookie
This is a cookie used to maintain the login session between the FinTechUI and the FinTechApi. As this maintains the login state of the PSU in the FinTechUI, this session can be kept open for the life span of the interaction between the FinTechUI and the FinTechApi.

### <a name="FinTechLoginSessionState"></a> FinTechLoginSessionState
This is the CSRF-State String associated with the FinTechLoginSessionCookie. This information must be presented whenever the FinTechApi consumes the FinTechLoginSessionCookie. It encodes a key that is used to encrypt/decrypt information stored in the corresponding FinTechLoginSessionCookie.

### <a name="Fintech2TppRedirectInfoPage"></a> Fintech2TppRedirectInfoPage
This panel will be used to inform the PSU upon redirecting the PSU to the ConsentAuthorisationApi. This information step is recommended as changes in UI display between the FinTechUI and the ConsentAuthorisationUI might confuse the PSU.     

## <a name="TppDC"></a> Tpp Data Center
Data center environment of the TPP

### <a name="TppBankingApi"></a> TppBankingApi
Tpp backend providing access to ASPSP banking functionality. This interface is not directly accessed by the PSU but by the FinTechApi. FinTechApi will use a FinTechContext to authenticate with the TppBankingApi.

### <a name="TppBankSearchApi"></a> TppBankSearchApi
Repository of banks maintained in the TPP's banking gateway. The banking search API will later presen an interface to configure profiles attached to listed banks.

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

### <a name="ConsentAuthSessionCookie"></a> ConsentAuthSessionCookie
This is the cookie object used to maintain the consent session between the ConsentAuthorisationUI and the ConsentAuthorisationApi. It will generated and set as a __httpOnly, Secure__

This cookie will generally contain the identifier of the TppContentSession and the cryptographic key used to read that TppContentSession.

### <a name="consentAuthState"></a> consentAuthState
This is the CSRF-State String associated with the ConsentAuthSessionCookie. It encodes a key that is used to encrypt information stored in the corresponding ConsentAuthSessionCookie.

This is: consentAuthState = state-id + consentEncryptionKey

All requests to the ConsentAuthorisationApi must always provide the consentAuthState as a __X-XRSF-Token__ and set a ConsentAuthSessionCookie as a cookie. 

Passing a consentAuthState to the UI.
- For 30X Redirect Requests, this is passed to the UI as a URL query param part of the redirect URL.
- For 20X Responses, this is part of the returned response body (AuthorizeResponse).

The consentAuthState shall never be stored in the ConsentAuthSessionCookie. 

As a redirect request carries the consentAuthState in parameter, a new consentAuthState shall be generated after each redirect and returned back to the client, as the old one is probably leaked into log files as part of a request URI.

### <a name="RedirectSession"> RedirectSession
Holds consent information for the duration of a redirect. Redirect patterns are described [below](dictionary.md#Redirection).

### <a name="RedirectSessionStoreApi"></a> RedirectSessionStoreApi
Storage of temporary redirect sessions. Redirect session are stored only for the duration of the redirect request while redirecting from the TppBankingApi to the ConsentAuthorisationApi and from the ConsentAuthorisationApi back to the TppBankingApi.

Consent Data might contain security sentive data like account number or payment information of the PSU. This is the reason why they will be encrypted prior to being temporarily held for the duration of the redirection in the RedirectSessionStoreApi. So the RedirectSessionStoreApi will generate a temporary authorization code that contains both the id of the redirect session and the key used to encrypt the content of the redirect session.

Upon request, the RedirectSessionStoreApi will use the provided authorization code to read and decrypt the consent session and will delete the consent session prior to returning it for the first time to the caller.

### <a name="BankingProtocol"></a> BankingProtocol
Component managing access to a banking interface initiative. WE will have to deal with many protocols like NextGenPSD2, HBCI, OpenBanking UK, PolishAPI.

### <a name="BankingProtocolSelector"></a> BankingProtocolSelector
Help select a banking protocol.

## <a name="AspspDC"></a> Aspsp Data Center
Data center environment of the ASPSP

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


