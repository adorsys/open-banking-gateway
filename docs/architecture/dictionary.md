# Dictionary

## <a name="PsuUserDevice"></a> PsuUserDevice

A PSU user device runs applications used by the PSU to access banking functionality. Those applications are generally called PsuUgerAgents.

## <a name="PsuUserAgent"></a> PsuUserAgent

Application running on a PSU device and used by the PSU to access banking functionality. We are describing the two main types of PsuUserAgents.

### WebBrowser

A Web browser is considered compliant in the context of this framework when it can protect specific information used between the PsuUserDevice and the corresponding server application to track the user session. For session tracking, this framework uses [Cookies RFC6265](https://tools.ietf.org/html/rfc6265). 

#### Security Considerations
The use of cookies provides the most elaborated way to protect a session established between a WebBrowser and server application. We assume a WebBrowser storing a cookie fulfills following requirements:
- Cookies carrying the attribute __HttpOnly__ are not provided access to scripts run by the UserAgent.
- Cookies carrying the attribute __Secure__ are only resent to the server over SSL connections.
- Expired Cookies (attribute __Expires__) are not resent to the server.
- Cookies shall never be transmitted to a domain not matching it's origin.

#### Redirection 
The server can request the WebBrowser to redirect the user to another page by returning a 30\[X\] response code to the WebBrowser. Redirection will generally happens in the same Browser environment. We will be using redirection to switch the user context from one application to another one. Following redirection will generally be found in this framework:
- FinTechApi to-> TppConsentSessionApi
- TppConsentSessionApi to-> AspspOnlineBankingAPI
- AspspOnlineBankingAPI backTo-> TppConsentSessionApi
- TppConsentSessionApi backTo-> FinTechApi

#### Redirection and Data Sharing
We assume all three applications FinTechApi, TppConsentSessionApi, AspspOnlineBankingAPI are hosted on different domains. This is, we are not expecting Cookies set by one application to be visible to another application (this might still happen on some local development environment, where everything runs on localhost). 
We also do not advice adding persistent information to __RedirectUrl__, as these are log files everywhere on infrastructure components in data centers. __RedirectUrl__ shall instead carry __OneTime__ and __ShortLived__ authorization code we call __code__, that can be used to retrieved shared payload through an authenticated back channel connection. This is the practice borrowed from [oAuth2 RFC6749](https://tools.ietf.org/html/rfc6749). Following table shows defined redirects and corresponding back chanel endpoints.

| Origin Application | Redirecting Application | Response Code; Location ; AuthCodeParam; Expiration | Redirect Target Application | Destination Application  | Data EndPoint at Origin Application |
| -- | -- | -- | -- | -- | -- |
| TppBankingApi | FinTechApi | 302 ; /auth ; code ; 5s | TppConsentSessionApi | TppConsentSessionApi | /loadTppConsentSession |
| TppConsentSessionApi | TppConsentSessionApi | Proprietary banking API. Assume RFC6749. /auth | AspspOnlineBankingAPI | AspspOnlineBankingAPI | none |
| AspspOnlineBankingAPI | AspspOnlineBankingAPI | 302 ; \[/ok\|/nok\] ; code ; 5s | TppConsentSessionApi | TppConsentSessionApi | /token |
| TppConsentSessionApi | TppConsentSessionApi | 302 ; \[/ok\|/nok\] ; code ; 5s | FinTechApi | TppBankingApi | /loadTppConsentSession |

#### Keeping Session Information
We assume all three applications FinTechApi, TppConsentSessionApi, AspspOnlineBankingAPI maintain their own session information. This framework uses following terms to name the session information held by an application on the UserAgent of the PSU.

| Application | SessionCookie |
|--|--|
| FinTechApi | Psu2FintechLoginSession |
| TppConsentSessionApi | Psu2TppConsentSession |
| AspspOnlineBankingAPI | Psu2AspspConsentSession |

Session information can also be kept across redirect life cycles. Upon redirecting the UserAgent to another application, the redirecting application can set Cookies that will be resent to the domain with future requests. This way, there will be no need to maintain user session information in temporary databases on the server, thus keeping server tiny.    

### Native App
The UserAgent might be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a user device: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636) 
For the purpose of kepping the overall architecture of this framework simple, we will require native applications to provide the same behavior as the WebBrowser described above.

### <a name="UserAgentContext"></a> UserAgentContext
All information associated with the PsuUserAgent. Like PSU-IP-Address, PSU-IP-Port, PSU-Accept, PSU-Accept-Charset, PSU-Accept-Encoding, PSU-Accept-Language, PSU-Device-ID, PSU-User-Agent, PSU-Geo-Location, PSU-Http-Method. Many backend API will require provisioning of the UserAgentContext to perform verification of the authenticity of the original PSU request and to customize the response produced for intermediary layers.

### <a name="FinTechUI"></a> FinTechUI
UI Application running on the PsuUserAgent and used by the PSU to access the FinTechApi

### <a name="TppConsentSessionUI"></a> TppConsentSessionUI
UI used by PSU to authorise consent in embedded case.

### <a name="AspsOnlineBankingUI"></a> AspsOnlineBankingUI
This UI manages the interaction between the PSU and the ASPSP in redirect cases.

## <a name="FinTechDC"></a> FinTechDC
Data center environment of the FinTech. Host the FinTechApi.

### <a name="FinTechApi"></a> FinTechApi
Financial web service provided by the FinTech.

### <a name="Psu2FinTechLoginSessionCookie"></a> Psu2FinTechLoginSessionCookie
This is a cookie used to maintain the login session between the FinTechUI and the FinTechApi. As this maintains the login state of the PSU in the FinTechUI, this session can be kept open for the life span of the interaction between the FinTechUI and the FinTechApi.

### <a name="FinTech2TppRedirectionInfoPanel"></a> FinTech2TppRedirectionInfoPanel
This panel will be used to inform the PSU upon redirecting the PSU to the TppConsentSessionApi. This information step is recommended as changes in UI display between the FinTechUI and the TppConsentSessionUI might confuse the PSU.     

## <a name="TppDC"></a> TppDC
Data center environment of the TPP

### <a name="TppBankingApi"></a> TppBankingApi
Tpp backend providing access to ASPSP banking functionality. This interface is not directly accessed by the PSU but by the FinTechApi. FinTechApi will use a FinTech2TppContext to authenticate with the TppBankingApi.

### <a name="TppBankSearchApi"></a> TppBankSearchApi
Repository of banks maintained in the TPP's banking gateway. The banking search API will later present an interface to configure profiles attached to listed banks.

### <a name="TppConsentSessionApi"></a> TppConsentSessionApi
This API is used to perform authorization and consent management for the PSU. In the embedded case, the PSU authorizes the consent via the TppConsentSessionUI. In case of redirection, the psu browser is redirected to the AspspOnlineBankingAPI so that the psu can authorize the consent.
### <a name="BankDescriptor"></a> BankDescriptor
Descriptive information associated with a bank like:
- The name of the Bank
- The address of the bank
- The bank identification code

### <a name="BankProfile"></a> BankProfile
BankingApi profile information associated with a bank like:
- The BankingProtocol used to connect with the bank
- List of Banking services provided by the BankingApi of the bank
- SCA approaches associated with the BankingApi
- ScaUIMetadaData: Screens and field used to collect user authentication data.
- Actions to be performed by the PSU before the Banking Protocol is used.


#### <a name="AisConsentSpec"></a> AisConsentSpec
Specification associated with an AisConsent. This is highly dependent on the BankProfile. Following information might be carried by an AisConsentSpec object:
- recurringIndicator
- validUntil
- frequencyPerDay
- combinedService
- accountAccessTemplate
- availableAccounts[availableAccountsWithBalances, allAccounts]
- allPsd2[allAccounts]

### <a name="FinTech2TppContext"></a> FinTech2TppContext
Information used to identify the FinTech application at the TppBankingApi. For example a FinTech SSL client certificate or an APIKey or an oAuth2 Password Grant Credential.

### <a name="FinTech2TppConsentSession"></a> FinTech2TppConsentSession
Information associated with the consent as exchanged between the FinTechApi and the TppBankingApi. Generally contain:
- Data needed to authorize the FinTechApi (FinTechSSLCertificate, ApiKey, SignedJWT)
- Data needed to customize psu access at the TppConsentSessionApi (showInfoPanel, fintechStateHash)
- Data needed to manage redirection of PSU from the TppConsentSession to the FintechUI like (FinTech-Redirect-URI, TPP-Nok-Redirect- URI, FinTech-Explicit- Authorisation- Preferred, FinTech-Content-Negotiation)

Object also contains information associated with the PSU requesting service if available.
- The identifier of the PSU in the realm of the Tpp FinTech2TppPsuIdentifier

#### <a name="FinTechAuth"></a> FinTechSSLCertificate, ApiKey, SignedJWT
These are credential used by a FinTech to identify themself at the interface of a TppBankingApi. This identifiers are obtained in negotiontiation between FinTech and Tpp prior to accessing the TppBankingApi

#### <a name="FinTech2TppPsuIdentifier"></a> FinTech2TppPsuIdentifier
This is the identifier of the PSU in the FinTech2Tpp relationship. This identifier can be saved once a consent has been sucessfully established to allow for reuse of existing consent in future sessions.

### <a name="Psu2TppConsentSessionCookie"></a> Psu2TppConsentSessionCookie
This is the cookie object used to maintain the consent session between the TppConsentSessionUI and the TppConsentSessionApi

### <a name="RedirectSessionStoreAPI"></a> RedirectSessionStoreAPI
Storage of temporary redirect sessions. Redirect session are stored only for the duration of the redirect request while redirecting from the TppBankingApi to the TppConsentSessionApi and from the TppConsentSessionApi back to the TppBankingApi.

Consent Data might contain security sentive data like account number or payment information of the PSU. This is the reason why they will be encrypted prior to being temporarily held for the duration of the redirection in the RedirectSessionStoreAPI. So the RedirectSessionStoreAPI will generate a temporary authorization code that contains both the id of the redirect session and the key used to encrypt the content of the redirect session.

Upon request, the RedirectSessionStoreAPI will use the provided authorization code to read and decrypt the consent session and will delete the consent session prior to returning it for the first time to the caller.

### <a name="BankingProtocol"></a> BankingProtocol
Component managing access to a banking interface initiative. WE will have to deal with many protocols like NextGenPSD2, HBCI, OpenBanking UK, PolishAPI.

### <a name="BankingProtocolSelector"></a> BankingProtocolSelector
Help select a banking protocol.

## <a name="AspspDC"></a> AspspDC
Data center environment of the ASPSP

### <a name="AspspBankingApi"></a> AspspBankingApi 
Api banking provided by ASPSP. This interface is not directly accessed by the PSU but by the TppBankingApi. TppBankingApi will use a Tpp2AspspContext to authenticate with the AspspBankingApi.

### <a name="Tpp2AspspContext"></a> Tpp2AspspContext
Information used to identify the Tpp application in the ASPSP environment. Like a TPP QWAC certificate.

### <a name="Tpp2AspspConsentSession"></a> Tpp2AspspConsentSession
Information associated with the consent initialized by the ASPSP. Containing ConsentId, ConsentData, AspspConsentSessionRedirectUrl, ...

### <a name="AspspOnlineBankingAPI"></a> AspspOnlineBankingAPI
Generally the online banking application on an ASPSP. In redirect cases, the ASPSP AspspOnlineBankingAPI establishes a direct session with the PSU to allow the PSU to identify himself, review and authorize the consent. 

### <a name="Psu2AspspConsentSession"></a> Psu2AspspConsentSession
This is a Cookie used to maintain the session between the AspsOnlineBankingUI and the AspspOnlineBankingAPI. As a recommendation, the validity of this Cookie shall be limited to the life span of the consent session. As the AspspOnlineBankingAPI redirects the PSU back to the TppConsentSessionApi up on completion of the consent session. Redirection happens independently on whether the consent was authorized or not.
 
### <a name="RedirectInfoPage"></a> RedirectInfoPage
It is recommended to inform the PSU prior to redirecting the PSU back to the TPP. This UI-Panel will be called RedirectInfoPage. If the ASPSP is using a trusted environment (Native App) and wants to keep the relationship to the PSU alive, it is necessary to store this relationship in a separated Psu2AspspLoginSession.

### <a name="Psu2AspspLoginSession"></a> Psu2AspspLoginSession
This Cookie will be used by the ASPSP to keep a login session of the PSU over the life span of consent session. This will prevent the PSU from performing the login step for upcoming consent sessions.

## <a name="ConsentData"></a> ConsentData    
Specification of the requested consent. BankAccount, frequencyPerDay, validUntil, ..., 

