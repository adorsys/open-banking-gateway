# Dictionary

## PsuUserDevice

A PSU user device runs applications used by the PSU to access banking functionality. Those applications are generally called PsuUgerAgents.

## PsuUserAgent

Application running on a PSU device and used by the PSU to access banking functionality. We are describing the two main types of PsuUserAgents.

### A WebBrowser

A Web browser is considered compliant in the context of this framework when it can protect specific information used between the PusUserDevice and the the corresponding server application to track the user session. For session tracking, this framework uses [Cookies RFC6265](https://tools.ietf.org/html/rfc6265). 

#### Security Considerations
The use of cookies provides the most elaborated way to protect a session established between a WebBrowser and server application. We assume a WebBrowser storing a cookie fulfills following requirements:
- Cookies carrying the attribute __HttpOnly__ are not provided access to scripts run by the UserAgent.
- Cookies carrying the attribute __Secure__ are only resent to the server over SSL connections.
- Expired Cookies (attribute __Expires__) are not resent to the server.
- Cookies shall never be transmitted to a domain not matching it's origin.

#### Redirection 
The server can request the WebBrowser to redirect the user to another page by returning a 30\[X\] response code to the WebBrowser. Redirection will generally happens in the same Browser environment. We will be using redirection to switch the user context from one application to another one. Following redirection will generally be found in this framework:
- FinTechApi to-> TppConsentSessionApi
- TppConsentSessionApi to-> AspspConsentSessionApi
- AspspConsentSessionApi backTo-> TppConsentSessionApi
- TppConsentSessionApi backTo-> FinTechApi

#### Redirection and Data Sharing
We assume all three applications FinTechApi, TppConsentSessionApi, AspspConsentSessionApi are hosted on different domains. This is, we are not expecting Cookies set by one application to be visible to another application (this might still happen on some local development environment, where everything runs on localhost). 
We also do not advice adding persistent information to __RedirectUrl__, as these are log files everywhere on infrastructure components in data centers. __RedirectUrl__ shall instead carry __OneTime__ and __ShortLived__ authorization code we call __code__, that can be used to retrieved shared payload through an authenticated back channel connection. This is the practice borrowed from [oAuth2 RFC6749](https://tools.ietf.org/html/rfc6749). Following table shows defined redirects and corresponding back chanel endpoints.

| Origin Application | Redirecting Application | Response Code; Location ; AuthCodeParam; Expiration | Redirect Target Application | Destination Application  | Data EndPoint at Origin Application |
| -- | -- | -- | -- | -- |
| TppBankingApi | FinTechApi | 302 ; /auth ; code ; 5s | TppConsentSessionApi | TppConsentSessionApi | /loadTppConsentSession |
| TppConsentSessionApi | TppConsentSessionApi | Proprietary banking API. Assume RFC6749. /auth | AspspConsentSessionApi | AspspConsentSessionApi | none |
| AspspConsentSessionApi | AspspConsentSessionApi | 302 ; \[/ok\|/nok\] ; code ; 5s | TppConsentSessionApi | TppConsentSessionApi | /token |
| TppConsentSessionApi | TppConsentSessionApi | 302 ; \[/ok\|/nok\] ; code ; 5s | FinTechApi | TppBankingApi | /loadTppConsentSession |

#### Keeping Session Information
We assume all three applications FinTechApi, TppConsentSessionApi, AspspConsentSessionApi maintain their own session information. This framework uses following terms to name the session information held by an application on the UserAgent of the PSU.

| Application | SessionCookie |
|--|--|
| FinTechApi | Psu2FintechLoginSession |
| TppConsentSessionApi | Psu2TppConsentSession |
| AspspConsentSessionApi | Psu2AspspConsentSession |

Session information can also be kept across redirect life cycles. Upon redirecting the UserAgent to another application, the redirecting application can set Cookies that will be resent to the domain with future requests. This way, there will be no need to maintain user session information in temporary databases on the server, thus keeping server tiny.    

### A Native App
The UserAgent might be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a user device: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636) 
For the purpose of kepping the overall architecture of this framework simple, we will require native applications to provide the same behavior as the WebBrowser described above.

### UserAgentContext
All information associated with the PsuUserAgent. Like PSU-IP-Address, PSU-IP-Port, PSU-Accept, PSU-Accept-Charset, PSU-Accept-Encoding, PSU-Accept-Language, PSU-Device-ID, PSU-User-Agent, PSU-Geo-Location, PSU-Http-Method. Many backend API will require provisioning of the UserAgentContext to perform verification of the authenticity of the original PSU request and to customize the response produced for intermediary layers.

## FinTechDC
Data center environment of the FinTech. Host the FinTechApi.

### FinTechApi
Financial web service provided by the FinTech.

### FinTechUI
UI Application running on the PsuUserAgent and used by the PSU to access the FinTechApi

### Psu2FintechLoginSession
This is a cookie used to maintain the login session between the FinTechUI and the FinTechApi. As this maintains the login state of the PSU in the FinTechUI, this session can be kept open for the life span of the interaction between the FinTechUI and the FinTechApi.

### FinTech2TppRedirectionInfoPanel
This panel will be used to inform the PSU upon redirecting the PSU to the TppConsentSessionApi. This information step is recommended as changes in UI display between the FinTechUI and the TppConsentSessionUI might confuse the PSU.     

## TppDC
Data center environment of the TPP

### TppBankingApi
Tpp backend providing access to ASPSP banking functionality. This interface is not directly accessed by the PSU but by the FinTechApi. FinTechApi will use a FinTech2TppContext to authenticate with the TppBankingApi.

### FinTech2TppContext
Information used to identify the FinTech application at the TppBankingApi. For example a FinTech SSL client certificate or an APIKey or an oAuth2 Password Grant Credential.

### FinTech2TppConsentSession
Information associated with the consent as exchanged between the FinTechApi and the TppBankingApi. Containing ConsentData, authCode, TppConsentSessionRedirectUrl (in the response), ...

### TppConsentSessionUI
UI used by PSU to authoraise consent in embedded case.

## AspspDC
Data center environment of the ASPSP

### AspspBankingApi 
Api banking provided by ASPSP. This interface is not directly accessed by the PSU but by the TppBankingApi. TppBankingApi will use a Tpp2AspspContext to authenticate with the TppBankingApi.

### Tpp2AspspContext
Information used to identify the Tpp application in the ASPSP environment. Like a TPP QWAC certificate.

### Tpp2AspspConsentData
Information associated with the consent initialized by the ASPSP. Containing ConsentId, ConsentData, AspspConsentSessionRedirectUrl, ...

### AspspConsentSessionApi
Generally the online banking application on an ASPSP. In redirect cases, the ASPSP AspspConsentSessionApi establishes a direct session with the PSU to allow the PSU to identify himself, review and authorize the consent. 

### AspspConsentSessionUI
This UI manages the interaction between the PSU and the ASPSP in redirect cases.

### Psu2AspspConsentSession
This is a Cookie used to maintain the session between the AspspConsentSessionUI and the AspspConsentSessionApi. As a recommendation, the validity of this Cookie shall be limited to the life span of the consent session. As the AspspConsentSessionApi redirects the PSU back to the TppConsentSessionApi up on completion of the consent session. Redirection happens independently on whether the consent was authorized or not.
 
### Aspsp2TppRedirectionInfoPanel
It is recommended to inform the PSU prior to redirecting the PSU back to the TPP. This UI-Panel will be called Aspsp2TppRedirectionInfoPanel. If the ASPSP is using a trusted environment (Native App) and wants to keep the relationship to the PSU alive, it is necessary to store this relationship in a separated Psu2AspspLoginSession.

### Psu2AspspLoginSession
This Cookie will be used by the ASPSP to keep a login session of the PSU over the life span of consent session. This will prevent the PSU from performing the login step for upcoming consent sessions.

## ConsentData    
Specification of the requested consent. BankAccount, frequencyPerDay, validUntil, ..., 
