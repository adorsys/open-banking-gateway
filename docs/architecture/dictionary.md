# Dictionary

- [AisConsentSpec](dictionary.md#AisConsentSpec)
- [App](dictionary.md#App)
- [AspspBankingApi](dictionary.md#AspspBankingApi)
- [BankDescriptor](dictionary.md#BankDescriptor)
- [BankingProtocol](dictionary.md#BankingProtocol)
- [BankProfile](dictionary.md#BankProfile)
- [Center](dictionary.md#Center)
- [Center](dictionary.md#Center)
- [ConsentAuthorisationApi](dictionary.md#ConsentAuthorisationApi)
- [ConsentAuthorisationUI](dictionary.md#ConsentAuthorisationUI)
- [ConsentAuthSessionCookie](dictionary.md#ConsentAuthSessionCookie)
- [consentAuthState](dictionary.md#consentAuthState)
- [ConsentData](dictionary.md#ConsentData)
- [Considerations](dictionary.md#Considerations)
- [Dictionary](dictionary.md#Dictionary)
- [Fintech2TppRedirectInfoPage](dictionary.md#Fintech2TppRedirectInfoPage)
- [FinTechApi](dictionary.md#FinTechApi)
- [FinTechContext](dictionary.md#FinTechContext)
- [FinTechDC](dictionary.md#FinTechDC)
- [FinTechLoginSessionCookie](dictionary.md#FinTechLoginSessionCookie)
- [FinTechLoginSessionState](dictionary.md#FinTechLoginSessionState)
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
- [Redirection](dictionary.md#Redirection)
- [RedirectSessionStoreApi](dictionary.md#RedirectSessionStoreApi)
- [RedirectSession](dictionary.md#RedirectSession)
- [Sharing](dictionary.md#Sharing)
- [TppBankingApi](dictionary.md#TppBankingApi)
- [TppBankSearchApi](dictionary.md#TppBankSearchApi)
- [TppConsentSession](dictionary.md#TppConsentSession)
- [TppContext](dictionary.md#TppContext)
- [TPP](dictionary.md#TPP)
- [UserAgentContext](dictionary.md#UserAgentContext)
- [WebBrowser](dictionary.md#WebBrowser)

## <a name="PSU"></a> PSU (Payment Service User)
A Payment Service User is any natural person that make use of a payment service on behalf of himself or on behalf of another legal person.

### Properties of a PSU
- A PSU is a natural person
- A PSU is in possession of some online banking credentials
- A PSU can use a application to interact with online services

### <a name="PsuIdentifier"></a> Identities of a PSU

In the context of open banking, a PSU might be interacting with up to three different legal entities.

![Component diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/components/psu-identities.puml&fmt=svg&vvv=1&sanitize=true)

 In general the OpenBanking contract is established between the the PSU, the TPP and the ASPSP. This contract is mapped in the following sentence: "__PSU__ __consents__ to the __ASPSP__ to service requests from a designated __TPP__ on his behalf. 
 
 Market practice is showing that special independent TPPs will be setup to service multiple FinTechs (Agents). This still does not change the OpenBanking contract, but add a layer of complexity on the design, As the TPP have to map the psu-id@fintech to the psu-id@tpp.
 
#### psu-id@fintech
This is the identity of the PSU in the realm of the FinTech, as most FinTech application require the PSU to establish an identity before using the application. FinTech must send this identity to the TPP with each request to allow the TPP to match it with eventually existing consents. 
 
#### psu-id@tpp
This is the PSU as known to the TPP. This identity only exists because some OpenBanking specifications do not allow TPP to maintain multiple valid consents of the same type on the same bank account. This is generally needed when a TPP services multiple FinTech.

In a situation where the TPP uses the same Account information consent to service many FinTech, a TPP consent management layer must allow a PSU to revoke that consent for a designated FinTech.
 
#### psu-id@aspsp
This is the identity of the PSU as known to the ASPSP. This identity generally matches an online banking identitfier in the realm of the ASPSP.

### <a name="ConsentData"></a> ConsentData    
In the context of OpenBanking, a consent encompasses all information necessary to provide a third party with the authorization to access banking services on behalf of the PSU. These are:
- PSU banking identifier information known as (psu-id@aspsp or psuId, psuCorporateId)
- PSU account details information (like account numbers, iban, ...)
- PSU payment orders (including beneficiary names, amounts, ...)
- PSU authentication methods

All these information are stored in different devices during the consent authorization session. Form of storages are among others:
- Held in the browser page for display to the PSU
- Stored in the Cookie for reuse by the corresponding backend
- Stored in backend databases for transfer to other server components
- Stored in backend databases for reuse by server components.

For the purpose of protecting these data, framework is designed to always have consent data encrypted while at rest or on transit. General logic is that encrypted payload and encryption key do not reside in the same environment, unless needed for decryption and processing of those data.

Following object hold consent data
- [TppConsentSession](dictionary.md#TppConsentSession): Stores consent data in the database of the TPP. 
- [RedirectSession](dictionary.md#RedirectSession) : Temporary storage of consent data when PSU is being redirected between FinTech, TPP and ASPSP.
- [PsuConsentSession](dictionary.md#PsuConsentSession) : This is only the reference to the TppConsentSession for a given FinTech. As the same TppConsentSession might be shared among many FinTechs.

## <a name="PsuUserDevice"></a> PsuUserDevice

A PSU user device runs applications used by the PSU to access banking functionality. Those applications are generally called PsuUgerAgents.

### <a name="PsuUserAgent"></a> PsuUserAgent

Application running on a PSU device and used by the PSU to access banking functionality. We are describing the two main types of PsuUserAgents.

#### WebBrowser

A Web browser is considered compliant in the context of this framework when it can protect specific information used between the PusUserDevice and the the corresponding server application to track the user session. For session tracking, this framework uses [Cookies RFC6265](https://tools.ietf.org/html/rfc6265). 

#### Native App
The UserAgent might be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a user device: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636).
For the purpose of kepping the overall architecture of this framework simple, we will require native applications to provide the same behavior as the WebBrowser described above.

### Security Considerations

#### <a name="UserAgentContext"></a> UserAgentContext
All information associated with the PsuUserAgent. Like PSU-IP-Address, PSU-IP-Port, PSU-Accept, PSU-Accept-Charset, PSU-Accept-Encoding, PSU-Accept-Language, PSU-Device-ID, PSU-User-Agent, PSU-Geo-Location, PSU-Http-Method. Many backend API require provisioning of the UserAgentContext to perform verification of the authenticity of the original PSU request and to customize the response produced for intermediary layers.

#### <a name="Cookies"></a> Cookies
The use of cookies provides the most elaborated way to protect a session established between a user agent and server application. We assume a user agent storing a cookie fulfills following requirements:
- The user agent store cookies on the user device in the context of a user session. Means if two user are sharing the same user device, cookies stored by those user wont be mixed up with each order.
- Cookies carrying the attribute __HttpOnly__ are not provided access to scripts run by the UserAgent. This is mostly vali for WebBrowser only as there is no way to enforce this in the world of native apps.
- Cookies carrying the attribute __Secure__ are only sent to the server over SSL connections.
- Expired Cookies (attribute __Expires__) are not sent to the server.
- Cookies shall never be transmitted to a domain not matching the configured domain.
- In the same domain, cookies shall only be transmitted to the given path

#### <a name="Redirection"></a> Redirection 
The server can request the user agent to redirect the user to another application by returning a 302 response code to the user agent. Redirection can generally happen in the same user agent environment (process) or can open a another user agent depending on the URL policy configured on the user device. For example depending on the location URL, a redirection from a WebBrowser might open a NativeApp or vice versa. For this reason, any information to be carried by the redirection has to be part of the location URL.
 
We will be using redirection to switch the user context from one application to another one. Following redirection will generally be found in this framework:
- FinTechApi to-> ConsentAuthorisationApi
- ConsentAuthorisationApi to-> OnlineBankingApi
- OnlineBankingApi backTo-> ConsentAuthorisationApi
- ConsentAuthorisationApi backTo-> FinTechApi

#### Redirection and Data Sharing
We assume all three applications FinTechApi, ConsentAuthorisationApi, OnlineBankingApi are hosted on different domains. This is, we are not expecting cookies set by one application to be visible to another application (this might still happen on some local development environment, where everything runs on localhost). 

We also do not advice adding persistent information to Location URL (__RedirectUrl__), as these are logged in files everywhere on infrastructure components in data centers.

* If we have any bulky information to share between the source application and the target application, we can add a __OneTime__ and __ShortLived__ authorization code we call __redirectCode__ to __RedirectUrl__. This redirectCode can be used to retrieved shared payload through an authenticated back channel connection. This is the practice borrowed from [oAuth2 RFC6749](https://tools.ietf.org/html/rfc6749). Following table shows defined redirects and corresponding back chanel endpoints.

* If we want to make sure the user is redirected back to the original user device, we can set a RedirectCookie while return 302to the source user agent. In this case the url used by the target user agent to redirect the user backto the source user agent must:
  * carry a path (e.g. /consent/{auth-id}) that allows retransmission of the redirectCookie to the source, and
  * carry a redirectState (e.g. /consent/{auth-id}/fromAspsp/{redirectState}) that can be used as XSRF token for the validation of the redirect cookie.

#### <a name="SessionCookie"></a> SessionCookie and XSRF
We assume all three applications FinTeApi, ConsentAuthorisationApi, OnlineBakingApi maintain their own session information with corresponding UIs. We assume those APIs use Cookies to maintain session with the user agent. In the context of this framework, those cookies are called SessionCookies.We also expect a following behavior from APIs and UserAgents:

* A response that sets a SessionCookie also carries a corresponding X-XSRF-TOKEN in the response header.
* A request that authenticates with a session cookie must also add the X-XSRF-TOKEN to the request header.

### <a name="PsuConsentSession"></a> PsuConsentSession
Once a consent authorization process is initiated by the TPP, we want to make sure that the PSU using the OnlineBanking interface of the ASPSP to give his consent in the redirect cases(psu-id@aspsp), or the PSU using the ConsentAuthorisationApi of the TPP to give his consent in the embedded case (psu-id@tpp) is the same as the PSU that is requesting the service on the FinTechApi interface (psu-id@fintech).

This is not obvious a malicious PSU can start a consent authorization process and trick another PSU to provide consent on the ASPSP interface. Following are measures used to prevent such impersonation:

#### Terminology
- __Consent Requesting Party (CRP)__: this is the source Party the redirect the PSU to a target Party for a consent request.
- __Consent Providing Party (CPP)__: This is the target party that collects the consent from the PSU.  
- __Example FinTech -> TPP__: If a FinTech redirects the PSU to the TPP, the FinTech is the consent requesting party and the TPP is the consent providing party.
- __Example TPP -> ASPSP__: If a TPP redirects the PSU to the ASPSP, the TPP is the consent requesting party and the ASPSP is the consent providing party.

#### Step-1: Store psu-id@fintech Consent Authorisation Session
When the PSU is requesting a service on the FinTech interface, identity of the PSU (psu-id@fintech) is sent with the service request to TppBankingApi. If there is no suitable consent present, the TPP application will initiate a consent authorization session through the FinTechApi. The first security measure to be taken consists in storing the service requesting PSU identity (psu-id@fintech) in the consent authorization session data. As this identity is under the control of the FinTech, service can only be provided to this PSU through this FinTech if corresponding consent is associated with this identity (psu-id@fintech) in the database of the TPP.

#### Step-2: Associate Granted Consent with psu-id@fintech 
With the association of the consent and the psu-id@fintech, we want to make sure that the PSU that provided the consent is the same as the one that initiated the consent on the FinTEch interface. __The Need__ to __associate__ applies for all sca approaches defined by OpenBanking specifications (embedded, redirect, oauth, decoupled). The way the association happens might nevertheless depend on many other factors:
* On whether the FinTech and the TPP are the same application
* On whether the FinTech and/orTPP can rely on the identity provided of the bank to identify the PSU.
* etc...

In this framework, we designed a two steps redirection FinTech -> TPP -> ASPSP knowing that this might make the process more cumbersome, but this design represents the superset of most of the cases found on the market. After thorough analysis of most scenarios, we noticed that the Consent2Identity association can only securely happen in one of these two ways:

#### Step-2 Alt-1: Sharing of IDP (Identity Provider)
If the __CRP__ (__FinTech__ -> TPP or __TPP__ -> ASPSP) shares the same identity provider with the CPP, a re-authentication of the PSU at the interface of the CPP (consent providing entity) will be sufficient to guaranty authenticity of the association. This means:
* in the case FinTech -> TPP, psu-id@fintech=psu-id@tpp. 
* in the case TPP -> ASPSP,psu-id@tpp=psu-id@aspsp.
In both cases, encoding the target identity in the consent token is sufficient to guaranty the association, as the consent token in keyed by the source identity in the TPP database.     

#### Step-2 Alt-2: No Sharing of IDP (Identity Provider)
If both __CRP (source)__ and __CPP (target)__ do no share identity provider, a back redirection from the __CPP__ to the __CRP__ must be used to finalized the consent association. Following sub-steps will be needed to ensure proper redirection:
* Set the RedirectCookie before redirecting a user from the __CRP__ to the __CPP__.
* Protect sharing of the __redirect back url__
  * In case there is an initiation step present between __CRP__ and __CPP__, use this step to transfer the redirect back url
  * In case there is such initiation step, or OAuth is being used, make sure oAuth2 __redirect back url templates__ and __webOrigins__ are properly designed as the redirect url is transported with the consent request and eposed to attackers manipulations.
* Design the redirect back url to contain a reference of the RedirectCookie and a corresponding XSRF-token (redirectState) for the validation of the RedirectCookie.

Once the PSU is redirected back to the __CRP__, a validation of the RedirectCookie is used to guaranty integrity of the overall process and finalize the association.

#### Anomalies of some OpenBanking Approaches
In some OpenBanking approaches, validating the consent at the OnlineBanking interface of the ASPSP will directly lead to the execution of the financial transaction (mostly in the case of payment initiation consent). This is a weakness in the process design as there can be no guaranty of a matching association between __PSU identity of CRP__ PSU and __PSU identity of CPP__ before the final verification of the identity coherence.

In order to fix this problem, concerned OpenBBAnkingApi will have to be modified to separate consent authorization from service request. Once a consent is authorized (and identity association is finalized), the FinTech/TPP will have to explicitly re-send the service request to the ASPSP. 

### Applications Running on a User Device

#### <a name="FinTechUI"></a> FinTechUI
UI Application running on the PsuUserAgent and used by the PSU to access the FinTechApi

#### <a name="ConsentAuthorisationUI"></a> ConsentAuthorisationUI
UI used by PSU to authorize consent in embedded case.

#### <a name="OnlineBankingUI"></a> OnlineBankingUI
This UI manages the interaction between the PSU and the ASPSP in redirect cases.

## <a name="FinTechDC"></a> FinTech Data Center
Data center environment of the FinTech. Host the FinTechApi.

### <a name="FinTechApi"></a> FinTechApi
Financial web service provided by the FinTech.

### <a name="FinTechLoginSessionCookie"></a> FinTechLoginSessionCookie
This is a SessionCookie used to maintain the login session between the FinTechUI and the FinTechApi. As this maintains the login state of the PSU in the FinTechUI, this session can be kept open for the life span of the interaction between the FinTechUI and the FinTechApi.

There is a X-XSRF-TOKEN String associated with the FinTechLoginSessionCookie. This information must be presented whenever the FinTechApi consumes the FinTechLoginSessionCookie. The X-XSRF-TOKEN encodes a key that is used to validate (evtl. encrypt/decrypt) information stored in the corresponding FinTechLoginSessionCookie.

## <a name="TppDC"></a> Tpp Data Center
Data center environment of the TPP

### <a name="TPP"></a> TPP
A TPP is a Third Party Provider.

### <a name="TppBankingApi"></a> TppBankingApi
Tpp backend providing access to ASPSP banking functionality. This interface is not directly accessed by the PSU but by the FinTechApi. FinTechApi will use a FinTechContext to authenticate with the TppBankingApi.

### <a name="TppBankSearchApi"></a> TppBankSearchApi
Repository of banks maintained in the TPP's banking gateway. The banking search API will later present an interface to configure profiles attached to listed banks.

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

FinTechContext might also contain FinTEch specific Information like:
- Data needed to customize PSU access at the ConsentAuthorisationApi (showInfoFlag)
- Data needed to manage redirection of PSU back from the ConsentAuthorizeApi to the FintechApi like (FinTech-Redirect-URI-Template, FinTech-Nok-Redirect-URI-Template, FinTech-Explicit-Authorisation-Preferred, FinTech-Content-Negotiation)

### <a name="ConsentAuthorisationApi"></a> ConsentAuthorisationApi 
Interface used by the PSU to authorize a consent.

### <a name="ConsentAuthSessionCookie"></a> ConsentAuthSessionCookie
This is a SessionCookie used to maintain the login session between the ConsentAuthorisationUI and the ConsentAuthorisationApi. As this maintains the login state of the PSU in the ConsentAuthorisationUI, this session can be kept open for the life span of the interaction between the ConsentAuthorisationUI and the ConsentAuthorisationApi.

There is a X-XSRF-TOKEN String associated with the ConsentAuthSessionCookie. This information must be presented whenever the ConsentAuthorisationApi consumes the ConsentAuthSessionCookie. The X-XSRF-TOKEN encodes a key that is used to validate (evtl. encrypt/decrypt) information stored in the corresponding ConsentAuthSessionCookie.

### <a name="RedirectSession"> RedirectSession
Holds consent information for the duration of a redirect. Redirect patterns are described [below](dictionary.md#Redirection).

### <a name="RedirectSessionStoreApi"></a> RedirectSessionStoreApi
Storage of temporary redirect sessions. Redirect session are stored only for the duration of the redirect request while redirecting from:
* FinTechApi to ConsentAuthorisationApi and validate when redirected back to secure coherance between FinTech and TPP identities.  
* ConsentAuthorisationApi to the OnlineBankingApi and validate when redirected back to secure coherance between TPP and ASPSP identities.

Consent Data might contain security sensitive data like account number or payment information of the PSU. This is the reason why they will be encrypted prior to being temporarily held for the duration of the redirection in the RedirectSessionStoreApi. So the RedirectSessionStoreApi will generate a temporary authorization code that contains both the id of the redirect session and the key used to encrypt the content of the redirect session.

Upon request, the RedirectSessionStoreApi will use the provided redirectCode to read and decrypt the consent session and will delete the consent session prior to returning it for the first time to the caller.

### <a name="BankingProtocol"></a> BankingProtocol
Component managing access to a banking interfaces. We will have to deal with many protocols like NextGenPSD2, HBCI, OpenBanking UK, PolishAPI. For design details, see [BankingProtocol](./drafts/initial_requirements.md)

## <a name="AspspDC"></a> Aspsp Data Center
Data center environment of the ASPSP

### <a name="AspspBankingApi"></a> AspspBankingApi 
Api banking provided by ASPSP. This interface is not directly accessed by the PSU but by the TppBankingApi. TppBankingApi will use a TppContext to authenticate with the TppBankingApi.

### <a name="TppContext"></a> TppContext
Information used to identify the Tpp application in the ASPSP environment. Like a TPP QWAC certificate.

### <a name="TppConsentSession"></a> TppConsentSession
Storage for consent data in the realm of the TPP accessible to both the TppBankingApi and the ConsentAuthorizationApi.

The cryptographic key needed to recover the TppConsentSession is always delivered by the calling layer. These are:
- FinTechUI -> FinTechApi -> TppBankingApi : in this case the key needed to recover the TppConsentSession in contained in the PsuConsentSession. Generally that key will transitively originate from an interaction with the user agent. 
- CosentAuthorizationUI -> CosentAuthorizationApi : in this case the key needed to recover the TppConsentSession originate from the ConsentAuthSessionCookie.

Beside consent data, additional data might be held in the TppConsentSession: 
- [FinTechContext](dictionary.md#FinTechContext): Data needed to authorize the FinTechApi (FinTechSSLCertificate, ApiKey, SignedJWT)
- Additional information needed for interaction between TPP and ASPSP but without any concern to the PSU.

### <a name="OnlineBankingApi"></a> OnlineBankingApi
Generally the online banking application on an ASPSP. In redirect cases, the ASPSP OnlineBankingApi establishes a direct session with the PSU to allow the PSU to identify himself, review and authorize the consent. 

