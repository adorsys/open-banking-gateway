# PSU Security Design

The purpose of this document if to analyze the security architecture of an OpenBanking based application from the perspective of an end user (PSU). 

## <a name="PSU"></a>PSU (Payment Service User)
A Payment Service User is any natural person that uses a payment service on behalf of himself or on behalf of another legal person.

### Properties of a PSU
Form the perspective of this analysis,
- A PSU is a natural person.
- A PSU is in possession of some personal non sharable online banking credentials.
- A PSU uses applications running on some devices to interact with online services.
- A PSU can act on behalf of himself, another natural or legal person.

### <a name="PsuIdentifier"></a>Identities of a PSU

In the context of OpenBanking, a PSU might be interacting with up to three different legal entities to consume a single banking service. In the service chain, we will generally find a [FinTech](dictionary.md#FinTech), a [TPP](dictionary.md#TPP) and the [ASPSP](dictionary.md#ASPSP). 

The OpenBanking contract establishes a relationship between the __PSU__, the __TPP__ and the __ASPSP__. This contract is mapped in the following sentence: "__PSU__ __authorizes__ the __ASPSP__ to service requests from a designated __TPP__ on his behalf".

Market practice shows that special independent TPPs will be setup to service multiple FinTechs (Agents). This still does not change the OpenBanking contract, but extends the contract with the following sentence: "__PSU__ also __authorizes__ the __TPP__ to  service requests from a designated __FinTech__ on his behalf.

At the end, we have the following service chain: PSU -> FinTech -> TPP -> ASPSP that might also require following interactions (PSU -> FinTech), (PSU -> TPP), (PSU -> ASPSP), resulting in the PSU having to maintain up to 3 different identities. The following picture displays the 3 different PSU access paths and identities.

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
A PsuUserDevice runs applications used by the PSU to access banking services. Those applications are generally called PsuUserAgents.

### <a name="PsuUserAgent"></a>PsuUserAgent
A PsuUserAgent is an application running on a PsuUserDevice and used by the PSU to access banking functionality. PsuUserAgents are either Web Applications running on a standard WebBrowser or Native Applications.

A WebBrowser is considered compliant with this framework when :
- it can protect access to Cookies.
- it can manage redirection as defined by the http specification. 

A UserAgent might also be a native application running on a user mobile device or a desktop computer. In this case, redirection might still take place, but with consideration of the physical transition between source and target UI-Application. Following specifications deal with security threads associated with the redirection between UI-Application on a PsuUserDevice: [RFC8252:OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252),[RFC7636:Proof Key for Code Exchange by OAuth Public Clients](https://tools.ietf.org/html/rfc7636).

For uniformity, we require native applications considered compliant with this framework to provide the same behavior as the Web Browser based PsuUgerAgents with respect to cookie management and redirection.

#### <a name="UserAgentContext"></a>UserAgentContext
Independent on the type of PsuUgerAgent, OpenBanking interfaces will require transmission of a class of information associated with the PsuUserAgent so they can perform verification of the authenticity of the original PSU request and customize the response produced for intermediary layers. We group these data under the name "UserAgentContext". Following header names account among the UserAgentContext: IP-Address, IP-Port, Accept, Accept-Charset, Accept-Encoding, Accept-Language, Device-ID, User-Agent, Geo-Location, Http-Method.

## Security Considerations
This work deals with details associated with the assurance that the natural person that authorizes the execution of a banking service at the TPP or ASPSP interface (using TPP or ASPSP credentials he owns) is the same natural person that initiated the service at the FinTech interface. This is, we want to prevent the owner of some online banking credentials to involuntarily use them to authorize a banking service.

#### <a name="Cookies"></a>Cookies
The first measure consist in protecting a session established between a PsuUgerAgent and each server application.

The use of [Cookies RFC6265](https://tools.ietf.org/html/rfc6265) provides the most elaborated way to protect a session established between a PsuUgerAgent and a ServerApplication (FinTechApi, ConsentAuthorizeApi). We assume a PsuUgerAgent storing a cookie fulfills following requirements:
- Cookies carrying the attribute __HttpOnly__ are not provided access to scripts run by a web browser based UserAgent. But this requirement can not be enforced for native applications. If they still have to fulfill this requirement, mobile applications designed for banking services must undergo thorough security reviews before released for public use.
- Cookies carrying the attribute __Secure__ are only sent to the server over SSL connections.
- Expired Cookies (attribute __Expires, Max-Age__) are not sent to the server.
- Cookies set with no value (__SessionCookie=;__) are not sent to the server.
- Cookies shall never be transmitted to a domain not matching it origin
- In the same domain, cookies shall only be transmitted to the configured path.

#### <a name="Redirection"></a> Redirection
The server can request the PsuUgerAgent to redirect the user to another application by returning an "HTTP 302" response code to the PsuUgerAgent. Redirection can generally happen in the same PsuUgerAgent environment (process) or can open a another PsuUgerAgent depending on the URL policy configured on the PsuUserDevice. The handling of redirection in native app environments is well handled in __[OAuth 2.0 for Native Apps RFC8252](https://tools.ietf.org/html/rfc8252)__.
 
We will be using redirection to switch the user context from one application to another one. Following redirection will generally be found in this framework:
- FinTechApi to-> ConsentAuthorisationApi
- ConsentAuthorisationApi to-> OnlineBankingApi
- OnlineBankingApi backTo-> ConsentAuthorisationApi
- ConsentAuthorisationApi backTo-> FinTechApi

#### <a name="Redirection_Data_Sharing"></a>Redirection and Data Sharing
We assume all three applications FinTechApi, ConsentAuthorisationApi, OnlineBankingApi are hosted on different domains. This is, we are not expecting cookies set by one application to be visible to another application. 

We also do not advice adding persistent information to Location URL (__RedirectUrl__), as these are logged in files everywhere on infrastructure components in data centers. Beside this, recall that any information to be carried to the target of the redirection has to be part of the location URL.

* If we have any bulky information to share between the source application and the target application, we can add a __OneTime__ and __ShortLived__ authorization code we call __redirectCode__ to __RedirectUrl__. This redirectCode can be used at target to retrieved shared payload (generally through an authenticated back channel connection). This is the practice borrowed from [oAuth2 RFC6749](https://tools.ietf.org/html/rfc6749).
* If we want to make sure the user is redirected back to the original PsuUserDevice, we can set a RedirectCookie while returning HTTP 302 to the source PsuUgerAgent. In this case, the URL used by the target PsuUgerAgent to redirect the user back to the source PsuUgerAgent must:
  * carry a path information (e.g. /consent/{auth-id}) that allows transmission of the previously stored RedirectCookie to the source, and
  * carry a redirectState (e.g. /consent/{auth-id}/fromAspsp/{redirectState}) that can be used as XSRF token for the validation of the redirect cookie.

#### <a name="SessionCookie"></a> SessionCookie and XSRF
We assume all three applications FinTeApi, ConsentAuthorisationApi, OnlineBakingApi maintain their own session information with corresponding UIs. We assume those APIs use cookies to maintain session with the corresponding PsuUgerAgents. In the context of this framework, those cookies are called SessionCookies. We also expect a following behavior from APIs and UserAgents:

* A response that sets a SessionCookie also carries a corresponding X-XSRF-TOKEN in the response header.
* A request that authenticates with a session cookie must also add the X-XSRF-TOKEN to the request header.

### <a name="SecurePsuConsent"></a>Securing the PSU Consent
When a PSU initiate a banking service at the FinTech interface (FinTechApi), the processing TPP will initiate a consent authorization process if needed (TppBankingApi). Once a consent authorization process is initiated by the TppBankingApi, we want to make sure that the PSU giving his consent (psu-id@tpp, psu-id@aspsp), is the same as the PSU requesting the service at the FinTech interface (psu-id@fintech).

Ensuring the equivalence of those identities can be represented as (assuming alice and bob are PSUs):

```
alice@fintech ==> alice@tpp ==> alice@aspsp 

// Where alice@fintech ==> alice@tpp neans the person identified at the FinTechApi as alice@fintech 
// owns the necessary credentials used to identify at the ConsentAuthorisationApi as alice@tpp

```
This is, the integrity of the OpenBanking solution is broken when security breaches can be used to compromise these equivalences like in the following pseudo code:

```
bob@fintech ==> alice@tpp ==> alice@aspsp 
// although in reality  bob@fintech =/=> alice@tpp 
// In this case bob identifies with the FinTechApi (as bob@fintech) but manipulates alice to identify with the ConsentAuthorisationApi and provide her consent for the requested banking service. 

bob@fintech ==> bob@tpp ==> alice@aspsp 
// although in reality  bob@tpp =/=> alice@aspsp
// In this case bob identifies with the FinTechApi and the ConsentAuthorisationApi but manipulates alice to identify with the OnlieBankingApi and provide her consent for the requested banking service. 

```

The purpose of this work is to make sure:
- In case of an SCA redirect/oauth/decoupled, the natural person (owner of an online banking account) that provides a his consent at the OnlineBankingApi of the ASPSP (alice@aspsp) is also the one that initiated the underlying banking service at the FinTech (alice@fintech). Whereby "__is also the one__" is equivalent to "__owns the necessary credentials__".
- In case of an SCA embedded, the natural person (owner of an online banking account) that provides her consent at the TTP ConsentAuthorisationApi (alice@tpp) is also the one that initiated the underlying banking service at the FinTechApi interface (alice@fintech). Whereby "__is also the one__" is equivalent to "__owns the necessary credentials__".
- In case of a multiple-sca, initiation of the underlying service might have happened else where. Still the natural person authorizing the service has to be identity equivalent to the inspecting the requested service at the FinTech interface.

We call this assurance __identity equivalence__. Ensuring integrity of the overall OpenBanking PSU workflow boils down to the __verification of the equivalence between involved identities psu-id@fintech ==> psu-id@tpp ==> psu-id@aspsp__. This is, following steps a judged necessary for a proper verification of the identity equivalence. 

#### Step-1: Identify PSU at the FinTechApi
We always expect the PSU to be identified at the FinTechApi interface. So we assume that the psu-id@fintech is known. The integrity of the rest of the framework relies on the capability of the FinTech to protect the session associating the PSU to the FinTechApi.

The service request itself is always initiated by the FinTechApi. Before initiation, we assume that the PSU (alice) has signed into the FinTechApi and her identity is known to the FinTechApi and thus associated with the service request forwarded to the TppBankingApi.

#### Step-2: Store psu-id@fintech with the ConsentAuthorizationSession
While processing a service request, if the TppBankingApi notices that the service request is not covered by a consent (either as a result of pre-checking the consent or from an error returned by the ASPSP's OpenBankingApi), the TppBankingApi will trigger a new consent authorization process identifies by an __auth-id__ and called __ConsentAuthorizationSession__. 

Starting a new ConsentAuthorizationSession, we require the TppBankingApi to store the identity of service requesting PSU alice@fintech with the ConsentAuthorizationSession record before initiating a redirect to the ConsentAuthorisationApi.

This is, the ConsentAuthorizationSession record stored in the TPP Database has the state: 

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, ConsentData]
// Where redirectCode is a one time key that can be used by the ConsentAuthorisationApi once to retrieve the ConsentAuthorizationSession.
// Where auth-id is the identifier of this ConsentAuthorizationSession

```

#### Step-2: Identify PSU at the TPP ConsentAuthorisationApi 
At first, redirecting a PSU from the FinTechApi to the TPP ConsentAuthorisationApi does not establish any relationship between the PSU and the TPP, even if we can use the redirectCode associated with the redirected URL to retrieve ConsentAuthorisationSession. Of course the ConsentAuthorisationApi knows that the ConsentAuthorizationSession was initiated by alice@fintech, but this does not mean that the PSU controlling the current PsuUserAgent (known as ConsentAuthorisationUI) is the same natural person as alice@fintech. 

In order to proceed with the ConsentAuthorisationSession, the TPP ConsentAuthorisationApi will have to establish an identification of the natural person controlling the UserAgent(implicit or explicit), resulting in a new PSU identity called (psu-id@tpp). This PSU identity (psu-id@tpp) is then associated with the ConsentAuthorisationSession upon successful authentication of the PSU at the TPP ConsentAuthorisationApi interface.

At this stage, the ConsentAuthorizationSession record stored in the TPP Database has the following state: 

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, alice123@tpp, ConsentData]
// But with the assumption that alice@fintech =/=> alice123@tpp. 
// Meaning that both identities are not yet verified equivalent (owned by the same natural person)

```

Even though the ConsentAuthorizationSession in the TPP database is associated with two identities (alice@fintech and alice123@tpp), there is no proof what so ever that alice@fintech and alice123@tpp are controlled by the same natural person.

#### Step-3a: Embedded ConsentAuthorisation at the TPP's ConsentAuthorisationApi
In an Embedded-SCA case, the psu-id@aspsp is collected by the ConsentAuthorisationApi and forwarded to the OpenBankingApi of the ASPSP. In this case it is easy to assume uniqueness between both psu-id@tpp and psu-id@aspsp as both are done withing the same user session at the same interface. This results in the following record after a successful embedded consent authorization at the ConsentAuthorisationApi:

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, alice123@tpp, alice-s@aspsp, ConsentData]
// alice@fintech =/=> alice123@tpp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)
// alice123@tpp ==> alice-s@aspsp. // Meaning alice123@tpp could provide the banking credentials of alice-s@aspsp.

```
Even in this embedded case, there is still a missing equivalence between __alice@fintech__ and __alice-s@tpp__.

#### Step-3b: Identify PSU at the ASPSP's OnlineBankingApi 

In a Redirect-SCA case (oauth, redirect, decoupled), the PSU has to be redirected by the ConsentAuthorisationApi to the OnlienBanking interface of the ASPSP. After a successful consent authorization at the OnlienBanking interface, the record could be updated by the mean of poling the authorization status of this ConsentAuthorizationSession at the OpenBankingApi of the ASPSP. Upon successful authorization of the requested consent, the ConsentAuthorizationSession in the databasde of the TPP looks like:   

```
[auth-id,redirectCode]=ConsentAuthorizationSession[auth-id,redirectCode, alice@fintech, alice123@tpp, alice-s@aspsp, ConsentData]
// alice@fintech =/=> alice123@tpp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)
// alice123@tpp =/=> alice-s@aspsp. // Meaning that both identities are not yet verified equivalent (owned by the same natural person)

```

#### Step-4: Verify Equivalence between psu-id@aspsp and psu-id@tpp
After a successful consent authorization at the OnlineBankingApi of the ASPSP, the framework has to ensure equivalence between the PSU identified at the TPP and the PSU identified at the ASPSP.

In this framework, we designed a two steps redirection FinTech -> TPP -> ASPSP knowing that this might make the process more cumbersome, but this design represents the superset of most of the cases found on the market. After thorough analysis of most scenarios, we noticed that the identity equivalence process can only securely happen in one of these ways:

##### Step-4 Alt-1: Sharing of Identity Provider
If TPP and ASPSP can rely on the same IDP to identify the PSU, the identity association process will be simple as the IDP will provide a common identifier. For example the subject claim of an id-token leading to subject(alice123@tpp)==subject(alice-s@aspsp).

##### Step-4 Alt-2: No Sharing of Identity Provider
If the TPP and ASPSP can not share the same identity provider, a __back redirection__ from the ASPSP to the TPP must be used to help complete the identity verification. Note that the physical __back redirection__ will not be possible with the decouple approach.

Following sub-steps will be needed to ensure clean and secure back redirection:
- A RedirectCookie must have been set on the PsuUserAgent while redirecting a user from the TPP (ConsentAuthorisationApi) to the ASPSP (OnlineBankingApi).
- The url for the __back redirection__ must have been protected against manipulation: 
  - In case there is an initiation step present between TPP and ASPSP, use this step must have been used to transfer the __back redirection url__ to the ASPSP (this is an example of the redirect approach of the NextGenPSD2 API).
  - In case there is no such initiation step (generally when OAuth is being used), make sure oAuth2 __back redirection url templates__ and __webOrigins__ are properly designed, as the concrete __back redirection url__ is transported in the __redirect_uri__ parameter of the consent request and exposed to attackers for manipulations.
- Design the __back redirection url__ to contain a reference of the RedirectCookie (__auth-id__) and a corresponding XSRF-token (__redirectState__) for the validation of the RedirectCookie. A sample url can look like: /consent/{__auth-id__}/fromAspsp/{__redirectState__}/ok. See [Redirection and Data Sharing](dictionary.md#Redirection_Data_Sharing) for more detail.

If a physical redirect can occur from the ASPSP (OnlineBankingApi) back to the TPP (ConsentAuthorisationApi), __a validation of the original RedirectCookie can be taken as a guaranty to declare equivalence between the psu-id@tpp and the psu-id@aspsp__.

##### Step-4 Alt-3: No Sharing of IDP and Decoupled Approach
For the decoupled approach, there is no way to provide a physical back redirect from the ASPSP to the TPP. So innovative data sharing methods are necessary to verify equivalence of both psu-id@tpp and psu-id@aspsp. This framework suggests the sharing of a QR-Code image as a mean of ensuring that the natural person in control of the ConsentAuthorisationApi is the same as the one in control of the OnlineBankingMobileApp instance used to execute the decoupled consent authorization against the OnlineBankingApi.
- If the PSU is using a desktop to request the banking service, the decoupled consent authorization on the OnlineBankingMobileApp will starts by the mean of the PSU scanning a QR-Code displayed by the ConsentAuthorisationUI.
- If both ConsentAuthorisationUI and OnlineBankingMobileApp are running on the same PsuUserDevice, App to App physical redirection will be preferable. Turning this decoupled approach into a redirect approach. If App to App physical redirection is not possible, mobile device image sharing routines can be used to push the QR-Code produced by the ConsentAuthorisationApi to the decoupled OnlineBankingMobileApp.

#### Step-5: Verify Equivalence between psu-id@tpp and psu-id@fintech
We assume that there is always a physical redirection possibility from the ConsentAuthorisationApi back to the FinTechApi. This assumption is based on the fact that there is a better synchronization between a TPP and FinTech using it's services. This is, the same principles described in "Step-4 Alt-1" and "Step-4 Alt-2" apply.

### Limitation of Identity Equivalence
In some OpenBanking approaches, validating the consent at the OnlineBanking interface of the ASPSP directly finalizes authorization of the service request. The current state of the NextGenPSD2 specification for example finalizes a payment initiation with the act of a PSU authorizing the payment consent (either on the ASPSP or TPP interface). But at this stage, the identity equivalence verification described above might not yet have happened, leaving the process incomplete and open for corruption.
 
This is a weakness in the process design as there can be no guaranty of a matching association between __PSU identity at TPP interface__ and __PSU identity at ASPSP interface__ before a final verification of the identity equivalence.

In order to fix this problem, concerned OpenBankingApi (like the NextGenPSD2) will have to be modified the protocol flow to allow the payment initiation request to be re-submited after verification of the identity equivalence (psu-id@fintech==> psu-id@tpp ==> psu-id@aspsp).

