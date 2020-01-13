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

## <a name="PSU"></a>PSU (Payment Service User)
A Payment Service User is any natural person that make uses a payment service on behalf of himself or on behalf of a legal person.

### Properties of a PSU
- A PSU is a natural person
- A PSU is in possession of some personal non sharable online banking credentials
- A PSU uses applications running on some devices to interact with online services

### <a name="PsuIdentifier"></a>Identities of a PSU

In the context of OpenBanking, a PSU might be interacting with up to three different legal entities to consume a single service. In the service chain, we will generally a [FinTech](dictionary.md#FinTech), a [TPP](dictionary.md#TPP) and the [ASPSP](dictionary.md#ASPSP). 

The OpenBanking contract establishes a relationship between the the __PSU__, the __TPP__ and the __ASPSP__. This contract is mapped in the following sentence: "__PSU__ __consents__ to the __ASPSP__ to service requests from a designated __TPP__ on his behalf".

Market practice shows that special independent TPPs will be setup to service multiple FinTechs (Agents). This still does not change the OpenBanking contract, extends the contract to the following sentence:"__PSU__ also __consents__ to the __TPP__ to  service requests from a designated __FinTech__ on his behalf.

At the end, we have the following service chain: PSU -> FinTech -> TPP -> ASPSP that might also require following interactions (PSU -> FinTech), (PSU -> TPP), (PSU -> ASPSP), resulting in the PSU having to maintain up to 3 different identities.

![Component diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/components/psu-identities.puml&fmt=svg&vvv=1&sanitize=true)
 
#### psu-id@fintech
This is the identity of the PSU in the realm of the FinTech, as most FinTech application require the PSU to establish an identity before using the application. This is the most important identity as it covers the consumption of the core services. Further identities (tpp, aspsp) are only needed for authorization. 

This framework is designed such as to request the FinTech to provide a permanent unique identity of each PSU to the TPP. This is what we call the __psu-id@fintech__ 
 
#### psu-id@tpp
This is the PSU as known to the TPP. If TPP environment is designed to service a single FinTech, this identity can be set equal to the __psu-id@fintech__.

If a TPPenvironment services multiple FinTech entities, the TPP will have to establish a separated PSU identity that references all corresponding FinTech identities. This requirement only exists because some OpenBanking specifications do not allow TPP to maintain multiple valid consents of the same type on the same bank account.

In a situation where the TPP uses the same account information consent to service many FinTech, a TPP consent management layer must allow a PSU to revoke that consent for a designated FinTech without revoking the consent at the ASPSP level. 
 
#### psu-id@aspsp
This is the identity of the PSU as known to the ASPSP. This identity generally matches an online banking identifier in the realm of the ASPSP.

## <a name="PsuUserDevice"></a>PsuUserDevice
A PsuUserDevice runs applications used by the PSU to access banking functionality. Those applications are generally called PsuUserAgents.

### <a name="PsuUserAgent"></a>PsuUserAgent
A PsuUserAgent is an application running on a user device and used by the PSU to access banking functionality. PsuUserAgents are either Web application running on a standard web browser or native applications.

We distinguish among two types of PsuUserAgents.

#### WebBrowser based UserAgent
A WebBrowser is considered compliant in the context of this framework when it can protect specific information used between the PsuUserDevice and the the corresponding server application to track the user session. For session tracking, this framework uses [Cookies RFC6265](https://tools.ietf.org/html/rfc6265). 

#### Native Applications
The UserAgent might be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a user device: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636).

For the purpose of keeping the overall architecture of this framework simple, we require native applications to provide the same behavior as the Web Browser based user agents described above.

#### <a name="UserAgentContext"></a> UserAgentContext
Independent on the type of user agent, OpenBanking interfaces will require a class of information associated with the PsuUserAgent so they can perform verification of the authenticity of the original PSU request and customize the response produced for intermediary layers. 
We group these Data under the name "UserAgentContext" and they and they are among other: IP-Address, IP-Port, Accept, Accept-Charset, Accept-Encoding, Accept-Language, Device-ID, User-Agent, Geo-Location, Http-Method.

### Security Considerations

#### <a name="Cookies"></a> Cookies
The use of cookies provides the most elaborated way to protect a session established between a user agent and server application. We assume a user agent storing a cookie fulfills following requirements:
- The user agent store cookies on the user device in the context of a user session. Means if two users are sharing the same user device, cookies stored by those users wont be mixed up with each order.
- Cookies carrying the attribute __HttpOnly__ are not provided access to scripts run by the browser based UserAgent. This requirement can not be enforced for mobile application. For that reason, mobile application will be passed under thorough security review before released for public use.
- Cookies carrying the attribute __Secure__ are only sent to the server over SSL connections.
- Expired Cookies (attribute __Expires__) are not sent to the server.
- Cookies shall never be transmitted to a domain not matching it origin
- In the same domain, cookies shall only be transmitted to the configured path.

#### <a name="Redirection"></a> Redirection
The server can request the user agent to redirect the user to another application by returning a 302 response code to the user agent. Redirection can generally happen in the same user agent environment (process) or can open a another user agent depending on the URL policy configured on the user device. For example depending on the protocol of the location URL, a redirection from a WebBrowser might open a NativeApp or vice versa. For this reason, any information to be carried by the redirection has to be part of the location URL.
 
We will be using redirection to switch the user context from one application to another one. Following redirection will generally be found in this framework:
- FinTechApi to-> ConsentAuthorisationApi
- ConsentAuthorisationApi to-> OnlineBankingApi
- OnlineBankingApi backTo-> ConsentAuthorisationApi
- ConsentAuthorisationApi backTo-> FinTechApi

#### Redirection and Data Sharing
We assume all three applications FinTechApi, ConsentAuthorisationApi, OnlineBankingApi are hosted on different domains. This is, we are not expecting cookies set by one application to be visible to another application (this might still happen on some local development environment, where everything runs on localhost). 

We also do not advice adding persistent information to Location URL (__RedirectUrl__), as these are logged in files everywhere on infrastructure components in data centers.

* If we have any bulky information to share between the source application and the target application, we can add a __OneTime__ and __ShortLived__ authorization code we call __redirectCode__ to __RedirectUrl__. This redirectCode can be used to retrieved shared payload through an authenticated back channel connection. This is the practice borrowed from [oAuth2 RFC6749](https://tools.ietf.org/html/rfc6749).
* If we want to make sure the user is redirected back to the original user device, we can set a RedirectCookie while returning 302 to the source user agent. In this case, the url used by the target user agent to redirect the user back to the source user agent must:
  * carry a path information (e.g. /consent/{auth-id}) that allows retransmission of the redirectCookie to the source, and
  * carry a redirectState (e.g. /consent/{auth-id}/fromAspsp/{redirectState}) that can be used as XSRF token for the validation of the redirect cookie.

#### <a name="SessionCookie"></a> SessionCookie and XSRF
We assume all three applications FinTeApi, ConsentAuthorisationApi, OnlineBakingApi maintain their own session information with corresponding UIs. We assume those APIs use cookies to maintain session with the corresponding user agents. In the context of this framework, those cookies are called SessionCookies. We also expect a following behavior from APIs and UserAgents:

* A response that sets a SessionCookie also carries a corresponding X-XSRF-TOKEN in the response header.
* A request that authenticates with a session cookie must also add the X-XSRF-TOKEN to the request header.

### <a name="PsuConsentSession"></a> PsuConsentSession
Once a consent authorization process is initiated by the TppBankingApi (Over the FinTechApi), we want to make sure that the PSU giving his consent (psu-id@tpp, psu-id@aspsp), is the same as the PSU requesting the service on the FinTechApi interface (psu-id@fintech).

Ensuring the equivalence of those identities can be represented as (assuming alice and bob are PSU):

```
alice@fintech -> alice@tpp -> alice@aspsp where alice refers to the same natural person (resp. online banking account) in all those realms.

```
This is the most challenging task to be solved by this framework. This is, the integrity of the OpenBanking solution is broken when following equivalences are achieved:

```
bob@fintech -> alice@tpp -> alice@aspsp

```
In this case bob identifies with the FinTechApi (as bob@fintech) but manipulates alice to identify with the ConsentAuthorisationApi and provide her consent for the requested banking service. 

```

bob@fintech -> bob@tpp -> alice@aspsp  

```
In this case bob identifies with the FinTechApi and the ConsentAuthorisationApi but manipulates alice to identify with the OnlieBankingApi and provide her consent for the requested banking service. 

The biggest challenge we will be facing is to make sure that:
- In case of an SCA redirect/oauth/decoupled, the natural person (online banking account) that provides a consent at the OnlineBankingApi of the ASPSP (alice@aspsp) is also the one that initiated the underlying banking service at the FinTech (alice@fintech)
- In case of an SCA embedded, the natural person (online banking account) that provides her consent at the TTP ConsentAuthorisationApi (alice@tpp) is also the one that initiated the underlying banking service at the FinTechApi interface (alice@fintech).

#### Step-1: Identify PSU at the FinTechApi
We always expect the PSU to be identified at the FinTechApi interface. So we assume that the psu-id@fintech is know. The integrity of the rest of the framework relys on the capability of the FinTech to protect the session associating the PSU to the FinTechApi.

The service request itself is always initiated by the FinTechApi. Before initiation, we assume that the PSU (alice) has signed into the FinTechApi and her identity is known to the FinTechApi and thus associated with the service request forwarded to the TppBankingApi.

#### Step-2: Store psu-id@fintech with the ConsentAuthorizationSession
While processing a service request, if the TppBankingApi notice that the service request is not covered by a consent (either as a result of pre-checking the consent or from an error return by the OpenBankingApi), the TppBankingApi will trigger a new ConsentAuthorizationSession. 

Starting a new ConsentAuthorizationSession, we require the TppBankingApi to store the identity of service requesting PSU alice@fintech with the ConsentAuthorizationSession record before initiating a redirect to the ConsentAuthorisationApi.

This is, the ConsentAuthorizationSession record stored in the TPP Database will have the form 

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, ConsentData]

```

where the redirect code can be used by the ConsentAuthorisationApi to retrieve the ConsentAuthorizationSession.

#### Step-2: Identify PSU at the TPP ConsentAuthorisationApi 
At first, redirecting a PSU from the FinTechApi to the TPP ConsentAuthorisationApi does not establish any relationship between the PSU and the TPP, even if we can use the redirectCode associated with the redirected url to retrieve ConsentAuthorisationSession. Off course the ConsentAuthorisationApi knows that the ConsentAuthorizationSession was initiated by alice@fintech, but this does not mean that the PSU controlling the current UserAgent is alice@fintech. 

In order to proceed with the ConsentAuthorisationSession, the TPP ConsentAuthorisationApi will have to establish a proper identification process (implicit or explicit), resulting in a new PSU identity called (psu-id@tpp). And this one will also be associated with the ConsentAuthorisationSession.

At this stage, the ConsentAuthorizationSession record stored in the TPP Database will have the form 

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, alice123@tpp, ConsentData]
alice@fintech =/= alice123@tpp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)

```

Even though the ConsentAuthorizationSession in the TPP databasde is associated with two identites (alice@fintech and alice123@tpp), there is no proof what so ever that alice@fintech and alice123@tpp are controlled by the same natural person.

The process continues with the addition of the psu-id@aspsp.

In an Embedded-SCA case, the psu-id@aspsp is collected by the ConsentAuthorisationApi and forwarded to the OpenBankingApi of the ASPSP. In this case it is easy to assume uniqueness between both psu-id@tpp and psu-id@aspsp. This is we will have the following record after a successful consent authorisation at the ConsentAuthorisationApi:

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, alice123@tpp, alice-s@aspsp, ConsentData]
alice@fintech =/= alice123@tpp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)
alice123@tpp ==> alice-s@aspsp. // Meaning alice123@tpp could provide the banking credentials of alice-s@aspsp.

```
Even in this embedded case, there is still a missing equivalence between alice@fintech and alice-s@aspsp.

#### Step-3: Identify PSU at the ASPSP OnlineBankingApi 

In a Redirect-SCA case (oauth, redirect, decoupled), the PSU will have to be redirected by the ConsentAuthorisationApi to the OnlienBanking interface of the ASPSP. After a successful consent authorization at the OnlienBanking interface, the record could be updated by the mean of poling the authorization status of this ConsentAuthorizationSession at the OpenBankingApi of the ASPSP. In this case the ConsentAuthorizationSession will look like:   

```
alice@fintech =/= alice123@tpp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)
alice123@tpp =/= alice-s@aspsp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)

```

#### Step-4: Verify Equivalence between psu-id@aspsp and psu-id@tpp
After a successful consent authorization at the OnlineBankingApi of the ASPSP, the framework has to ensure equivalence between the PSU identified at the TPP and the PSU identified at the ASPSP.

In this framework, we designed a two steps redirection FinTech -> TPP -> ASPSP knowing that this might make the process more cumbersome, but this design represents the superset of most of the cases found on the market. After thorough analysis of most scenarios, we noticed that the identity equivalence process can only securely happen in one of these two ways:

##### Step-4 Alt-1: Sharing of IDP (Identity Provider)
If TPP and ASPSP can rely on the same IDP to identify the PSU, the identity association process will be simple as the IDP will provide a common identifier subject(alice123@tpp)==subject(alice-s@aspsp).

##### Step-4 Alt-2: No Sharing of IDP (Identity Provider)
If the TPP and ASPSP can not share the same identity provider, a back redirection from the ASPSP to the TPP must be used to help complete the identity verification. Note that the physical __back redirection__ will not be possible in the decouple approach.

Following sub-steps will be needed to ensure proper redirection:
- A RedirectCookie must have been set while redirecting a user from the TPP (ConsentAuthorisationApi) to the ASPSP (OnlineBankingApi).
- The url for the __back redirection__ must have been protected against manipulation: 
  - In case there is an initiation step present between TPP and ASPSP, use this step to transfer the __back redirection url__ to the ASPSP (this is an example of the redirect approach of the NextGenPSD2 API).
  - In case there is no such initiation step (generally when OAuth is being used), make sure oAuth2 __back redirection url templates__ and __webOrigins__ are properly designed as the concrete __back redirection url__ is transported with the consent request and exposed to attackers manipulations.
- Design the __back redirection url__ to contain a reference of the RedirectCookie (auth-id) and a corresponding XSRF-token (redirectState) for the validation of the RedirectCookie.

If a physical redirect can occur from the ASPSP (OnlineBankingApi) back to the TPP (ConsentAuthorisationApi), a validation of the original RedirectCookie can be taken as a guaranty to declare equivalence between the psu-id@tpp and the psu-id@aspsp.

##### Step-4 Alt-3: No Sharing of IDP and Decoupled Approach
For the decoupled approach, there is no way to provide physical a back redirect from the ASPSP to the TPP. So innovative data sharing methods are necessary to verify equivalence of both TPP and ASPSP PSU-identities. This framework suggests the sharing of a QR-Code image as a mean of ensuring that the natural person in control of the ConsentAuthorisationApi is the same as the one in control of the OnlineBankingMobileApp used to execute the decoupled consent authorization against the online banking API.
- If the PSU is using a desktop to request the banking service, the decoupled consent authorization on the OnlineBankingMobileApp will starts by mean of the PSU scanning a QR-Code displayed on the user desktop by the ConsentAuthorisationUI.
- If both ConsentAuthorisationUI and OnlineBankingMobileApp are running on the same user device, App to App physical redirection will be preferable. Turning this decoupled approach into a redirect approach. If App to App h=physical redirection is not possible, mobile device image sharing routines can be used to push the QR-Code produced by the ConsentAuthorisationApi to the decoupled OnlineBankingMobileApp.

#### Step-5: Verify Equivalence between psu-id@tpp and psu-id@fintech
We assume that there is a physical redirection possibility from the ConsentAuthorisationApi back to the FinTechApi. This assumption is based on the fact that there is a better synchronization between a TPP and FinTech using it's services. This is, the same principles described in Step-4 Alt-1: Sharing of IDP and Step-4 Alt-2: No Sharing of IDP applies.

#### Consent Authorization, Identity Equivalence and Service Execution
In some OpenBanking approaches, validating the consent at the OnlineBanking interface of the ASPSP directly finalizes authorization of the service request. The NextGenPSD2 specification finalizes a payment initiation with the act of a PSU authorizing the payment consent (either on the ASPSP or TPP interface). But at this stage, the identity equivalence verification described above might not have happened yet leaving the process incomplete.
 
This is a weakness in the process design as there can be no guaranty of a matching association between __PSU identity of the TPP__ PSU and __PSU identity of ASPSP__ before a final verification of the identity equivalence.

In order to fix this problem, concerned OpenBankingApi (like the NextGenPSD2) will have to be modified to separate consent authorization from service request. 
- A consent authorization is always finalized with the verification of the identity equivalence (psu-id@fintech==> psu-id@tpp ==> psu-id@aspsp).
- The FinTechApi will then re-send the service request to the TppBankingApi
- TppBankingApi will only forward service request to ASPSP's OpenBankingApi if the corresponding consent is legitimate (meaning if identity equivalence verification was successful).

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

