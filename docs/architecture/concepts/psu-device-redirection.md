# UserAgent Redirection

## Abstract
Platform business is generally characterized by multiple applications that collaborate with each other by the mean of sharing data and functionality to provide more valuable services. The sharing of identity is a service that is being intensively used by online service providers to reduce the quantity of accounts to be managed by a single user. A PFM (Personal Finance Manager) application will load user banking transactions and analyze them to help the user have a better insight on his financial activities. As user banking transaction data are held by the bank, data sharing will occur by the mean of the user allowing his bank to share his banking transactions with the PFM provider.

The raise of online business platforms is happening from all corners of the world. In some regions, platform business is driven by regulators mandating custodian of user data to share these at the user's will with third parties. In some other regions, platform business initiative is driven by market participants. In some regions, data sharing initiatives are limited to financial or even only payment account data, while in some others regions, data sharing initiatives extends their scope to many other domains like telecommunication, health, utilities etc...

Although sharing data has always been practices in the business environment (see EBICS), the novelty here is the capacity of ad-hoc online connectivity of business, that strive to enable data sharing without time expensive preceding authorization steps at the agency location of the custodian. In the case of EBICS, the data owner will have to go to the bank and explicitly give an authorization to release his data to a designated third party. In a world dominated by real time online access to applications, authorization steps want to be done on the go and in real time. This is why the online platform business community leverages __UserAgent Redirection__ to allow for real time online connectivity and interaction between the user and many participating service providers.

The first intensive use of __UserAgent Redirection__ was battle tested with the adoption of oAuth2 and OpenID Connect. For those use cases, the primary purpose of redirection is the sharing of identity and the authorization to share identity related information. In the online platform business domain, __UserAgent Redirection__ is being used to manage authorization and sharing of functionality far beyond identity, like the delegation of fundamental personal authoritative permissions such as allowing a third party to initiate a payment from the user's bank account.

Independent on whether the __UserAgent Redirection__ is being used for online real time identity sharing or for more advanced authorization frameworks, it is essential to make sure that the natural person being redirected from one application to another to express his will __can not be impersonated__ by any mean (technical or social engineering). 

### Common Terms
Let's call:
- the __SOURCE_APP__ the application that redirects the user to another application (the __TARGET_APP__),
- the __SOURCE_UI__ the UserAgent used by the user to access the source application,
- the __TARGET_UI__ the UserAgent used by the user to access the target application,
- an __AUTOMATIC_REDIRECTION__ a situation in which the __SOURCE_UI__ __automatically__ presents the __TARGET_UI__ to the user. This generally happens when __SOURCE_UI__ and __TARGET_UI__ are on the same user device.
- a __MANUAL_REDIRECTION__ a situation in which the __SOURCE_UI__ __instructs__ the user to open the __TARGET_UI__. Even if for example a push notification is used to help the user activate the __TARGET_UI__, we consider it a manual redirection.
- an __AUTOMATIC_TRSP_MESSAGE__ a message automatically transported from the __SOURCE_APP__ to the __TARGET_APP__ without explicit intervention of the user. E.g.: A query parameter attached to the redirection URL; An information carried by the push notification sent to activate the TARGET_UI.
- a __MANUAL_TRSP_MESSAGE__ a message explicitly collected from the __SOURCE_UI__ and input into the __TARGET_UI__ by the user. E.g.:
  - A user using the __TARGET_UI__ to scan a QR_CODE;
  - A user sharing an Image from the __SOURCE_UI__ to the __TARGET_UI__;
  - A user typing a number in the __TARGET_UI__.

### Identity Equivalence
We want to make sure the natural person controlling access to __SOURCE_UI__ (__psu-id@target__) is the same natural person controlling access to the __TARGET_UI__ (__psu-id@target__). We will call this verification the __identity equivalence__. This identity equivalence seats at the core of the integrity of __UserAgent Redirection__ based online business interactions between providers. 

## Sharing Identity (oAuth)
An online business service provider generally implements functionality using clients and server side applications. It doesn't matter if biggest part of the application is run on the client side (UI) or on the server side (APP), both sides are generally found in almost all online business applications. This is, the typical design of an online business application looks like:

![Client Server Layout](../../img/redir/client-server.png)

Each application (APP) presents a UserAgent to the user. The identifier user@app-1 is used by APP-1 to manage the session between the UI-1 and APP-1 (resp. user@app-1 for UI-2 and APP-2). The following picture shows the delegation of identity management to an identity provider.

![IDP Layout](../../img/redir/idp-client-server.png)

In this case, APP-1 and APP-2 delegate authentication to the same identity provider and thus share the same identity (user@idp). Each delegation process generally involves redirection. In order to make sure that redirection based delegation process will not lead to an impersonation of the user, well defined protocols like oAuth2 are adopted by a wide part of the market. Nevertheless, even the most secure identity provider will not prevent badly connected applications (relying party) to offer space for the impersonation of the end user. The following picture display the classical oAuth2 workflow.

![oAuth2 Flow](../../img/redir/oauth2-flow.png)

The workflow displayed above shows an identity sharing process bases on oAuth2. Nothing states that both UI applications are on the same user device. In the rest of this document, we will assume __AUTOMATIC_REDIRECTION__ when both UI-RP and UI-IDP are on the same device and __MANUAL_REDIRECTION__ when both are not on the saem device.

### Automatic Redirection to the Identity Provider
This is a situation in which the __SOURCE_UI__ __automatically__ presents the __TARGET_UI__ to the user. This generally happens when both __SOURCE_UI__ and __TARGET_UI__ are on the same user device, even if they are not in the same web browser. This is what oAuth was developed for. There are many technologies used to enable automatic redirection on devices.
- HTTP redirect (30x) are used to automatically instruct web browser to redirect the user to another page. It is nevertheless worth to mention that this only works when the originating request sent to the APP by the UI is not an XHR-Request. In case of XHR-Request, redirection has to be handled by the JavaScript Framework providing the XHR functionality.
- __iOS Universal Link__ and __android App Links__ are used by the corresponding operating systems to automatically launch the TARGET_UI.
- __custom URI schemes__ are also used to instruct the device to launch a dedicated __TARGET_UI__ application. Whereby Universal Links resp. (App Links) are a better way to proceed as the request will be forwarded to the APP if the UI is not installed on the user device yet. 

The authorization flow starts by the mean of the relying party (RP) redirecting the user to the identity provider. Like we see on the picture, the source of the redirection would like to transport some information to the target of the redirection. The way information is transported highly depend on the nature of the redirection.

In the case of __AUTOMATIC_REDIRECTION__, information are transported as URL parameters. We have to be aware that there is no way to protect information transported from a UI application to another UI application on the user device. Any of those parameters can be modified by malicious program code running on the user device. In order to prevent modification of redirected parameter information, oAuth can be extended with an initiation step {0. initiate(user@idp?)} that will simplify implementation and increase robustness of the oAuth protocol.

If we do not have an initialization step and as we know there is no way to control the integrity of an information passed by one UI application to another one on a user device, some technical protection means must be used to make sure the control will not be returned to the wrong UI application. The type of protection used depends on the nature and the purpose of the information: 

- The __redirect_uri__: is generally used to instruct the IDP on where to send the user back after identification. If this information is changed on the way to the IDP, there is a risk the IDP redirects the control back to the wrong user agent. If the flow is not using an initialization step, the pre-configured URL-Template will have to be registered by the RP with the IDP before any delegation of authentication happens. Many technology initiatives propose way of preventing this sort of impersonation. Among other: PKCE, the use of a __RedirectCookie__ to hold a state inside the UI-RP. So that APP-RP can guaranty that control has been given back to original UI-RP instance controlled by ALICE (Step  3.) and not to a UI-RP controlled by BOB (Step  3a.).

- The __state__: is generally used to hold the other half of the state information. This can be processed by the RP upon receiving the back redirect request from the IDP. It is very usual to see application use the state parameter to validate information contained in the cookie and thus even prevent further attacks like __XSRF__ that can target the unauthorized reuse of the RedirectCookie by third party. This is why we advice to have the state carry a XSRF protection parameter.

### Manual Redirection to the Identity Provider
Let assume the IDP-UI is a NativeApp and the APP-RP suspects the identity of the user is (user@idp), then the redirection can occur by the mean of APP-RP instructing APP-IDP to send a push notification to UI-IDP. The user will then manually touch the notification message to open UI-IDP and identify with APP-IDP.

This initiation of a __MANUAL_REDIRECTION__ can be perceived as an initiation step as well: {0. initiate(user@idp?)}. Despite the case of a automatic redirection, the presence of a suspected user@idp is necessary to discover the device on which to start the authorization.

A manual opening of the __TARGET_UI__ indicates by no mean that the user is in control of both __SOURCE_UI__ and __TARGET_UI__. In order to make sure the user controls both environments, it is necessary to have the user manually collect some information displayed by the __SOURCE_UI__ and enter them in the __TARGET_UI__. We call this __Device Linking__. The way the display and collection process works depends on the nature of both __SOURCE_UI__ and __TARGET_UI__.
- If for example the __SOURCE_UI__ is a browser application running on a desktop computer and the __TARGET_UI__ is a NativeApp running on a mobile phone, the __TARGET_UI__ can provide tools for __scanning a QR_CODE__ displayed by the __SOURCE_UI__.
- The __TARGET_UI__ might as well just request the user to enter a sequence of digit displayed by the __SOURCE_UI__.
Again, __linking devices__ in manual redirection case is essential to make sure the same user is in control of both UIs.

### Automatic Return of Control to the Relying Party
The user identifies with the APP-IDP and there is a session between UI-IDP and APP-IDP. The IDP must return control to the relying party so RP can assert user identity and proceed with the service request.

In an __AUTOMATIC_REDIRECTION__ case, where UI-IDP and UI-RP are on the same device, a technical redirect can be used to send control back to RP. As describe above HTPP 30x, Universal Link, App Link and custom URI are all means used to automatically pass control from one application to another one. As these technical methods are all URL based, URL parameters can be used to transport information to the __TARGET_UI__. We also display above that there is no way to control the integrity of an information passed by one application to another one on a user device. The IDP generally sends two information back to the RP:

- The __state__ parameter sent back as received by the IDP and used to validate that control was sent back to the UI instance that initiated the authentication flow.
- The __code__ parameter sent by the IDP and used by the RP to retrieve the final authorization token.

### Manual Return of Control to the Relying Party
A manual return of control to the RP can happen in the background by having the IDP produce the token and send it in a back channel to the RP, and makes it the responsibility of the RP to activate the UI-RP. Activation can be done by the mean of a push notification as the APP-RP has already establish a link with APP-UI. In this case, the information collected will service as a __code__ used to collect the final authorization token from the IDP.

### Confirmation Step
In both redirection mode (automatic and manual), there is a need to implement the confirmation step that produces the final Token used to authorize access to user resources. Tunneling the __code__ trough the UI-RP to the APP-RP and then to the APP-IDP helps close a circle that shows that an information produced by the APP-IDP and returned to APP-UI, could be used by APP-RP to retrieve the authorization token. Last performance necessary to make this scheme secure is the task of the RP to verify the __UI-IDP__ transported the __code__ to the __originating UI-RP__. This last step is done by keeping an information in the UI-RP that is returned to the APP-RP with the __code__. 

### Beside Redirection
The oAuth framework defines many models of interaction between IDP and RPs. Beside the redirection base authorization flows (implicit and authorization code), the password grant flow is one that allows a relying party to collect user credentials and forward them to the identity provider. This flow generally assumes the RP is trusted and will not leak transported user credentials.

Without redirection, there is no need to fear any impersonation of the user, as the RP that collects user credential maintains a session with the user.
 
### Extending Identity Sharing to Advanced Use Cases
The experience realized with sharing identity in online business interaction has lead to an attempt to share even more data and functionality. Many online business platform initiatives are reusing the experience made over the last decade with oAuth, OpenId Connect and other __Identity Sharing__ schemes to define more advanced authorization frameworks that enable the sharing of other data and service on behalf of an end user. In most OpenBanking approaches evolving out there, a banking customer can authorize the custodian of his bank account (the bank) to open some functionalities to third parties. This way a third party provider of online services might pull the list of transactions of the end user's bank account or even initiate a payment on behalf of that end user.

The way a banking user give his consent to the bank is very similar to the way an online use allow the IDP to share his identity with a relying party. As these authorization frameworks are reaching the banking industry, there is an imminent need of providing a very clear understanding of how they work, so we can reduce the number of erroneous implementation on the market. A there will be a bigger incentive for malicious parties to try to exploit weaknesses associated with the implementation of those authorization frameworks.

## Fundamental of a Redirect
The purpose of this work is therefore to enlighten mechanism used in the process of redirecting a PSU from one UserAgent to another one.

### HTTP vs. XHR Redirect
The HTTP protocol was originally designed to support simple hypertext content production. In this context, necessary intelligence was delegated to the container displaying the produced content. This is the reason why a web browser processing a http response for display will follow 30x responses and proceed with another request to the provided location.

Using javascript to build more interactive browser applications, the XHR protocol was developed as an additional request response protocol between a web browser and a web server. With XHR, redirection is a little more complicated, as the Browser-Api still follows the redirect, but do not display the response in the browser window. Off course the javascript framework used will help read and display the response. But the redirect URL exposed with xhr.responseURL will not contain all original redirect parameters. See [atomic-http-redirect-handling](https://fetch.spec.whatwg.org/#atomic-http-redirect-handling) for details on why underlying browser APIs adopt this behavior.

Beyond web browser, native application are known to have full access on the HTTP Request/Response object. Corresponding HTTP client implementation expose more detailed API that allow to control the behavior of the redirect request. This, we will model the behavior of a redirect in API taking in consideration constraints of browser implementations and ignoring the existence of native applications, knowing that the will be able to provide the behavior expected from browser implementations.

Based on the facts described above, there is 2 options to choose from while implementing a redirect:
- __Given Full Control to User Agent__: if we want the user agent to have full control on the redirect process, we will have to return a __20x and a Location__ response to the user agent and instruct the user agent to proceed to the provided location url. This approach will not work for non javascript enhanced browser based UserAgents.
- __Preventing Browser Based Agent from accessing URL params__: Exposing url params to browser based UserAgent might open room for cros side scripting. If the framework is intending to add any sort of secret information to the URL, return a __30x and Location__ will be the best alternative as native browser API will follow the redirect before returning final response to the UserAGent.

User Experience Guide require a user to be notified prior to redirecting the user to another domain. This step shall generally be done in a proper step (rather than abandoned to the UI). By presenting a redirect-info-page to the user, following action can be design to simplify the implementation of the redirect request. E.g.: the redirect info page will offer a confirmation to the user and user a simple HTTP request (no XHR) to trigger the redirect process.

### Response Code Option
Advanced API design can allow the UI to decide if the redirect response has to be controlled by a 20x or a 30x response code.  

## Redirect for Consent Authorization of Banking Services
In OpenBanking initiatives, redirection is generally used to send a payment service user (PSU) to the TPP site, where TPP can authorize the execution of a banking service. In some situation, a TPP will even redirect the PSU to the banking site, so the PSU can interact directly with his bank for the consent authorization. In addition to using those three interfaces, the PSU might sometimes have to be redirected by the TPP or by his bank to the PSU mobile device to collect some OTPs. The following picture shows the PSU interfaces with all FinTech, TPP and Bank.

![High level architecture](../../img/open-banking-gateway-psu-interface.png)

Without redirection, PSU will have to manually provide authorization to each party, in the worse case, physically, having to go to the bank or third party's agency location.

With redirection, we gain in speed and usability, but care has to be taken no to provide room for impersonation of the PSU. The purpose of this section is describe steps necessary to perform redirection without risk of impersonating the PSU.

In the analysis, we identified two types of redirections:
- API redirection, where the RedirectUrl directly points to a server API
- UI redirection, where the redirect link either starts a native application on the user device of load the UI files from the designated content server. 

In all cases, we assume that UI file servers do not share the same domain (origin) as their corresponding API server. This is, we do not expect UI file servers to receive/process cookies set by API servers. The following picture displays 4 possible scenarios.

![High level architecture](../../img/redir/All-Redirs.png)

All 4 scenarios implement the same process of a redirect from a FinTech application to a TPP application for having the PSU authorizing a banking service, and a redirect back from the TPP to the FinTech with a confirmation code. Theoretically none of those redirects can ever be protected, as there can't be any deterministic assumption on the nature and the state of the user device. Even when there is knowledge on the nature of a user device, it is difficult to determine the current state of the device. We can't know if a user mistakenly installed a malware on his device.

### Step-1 Service Request
The process always starts with a banking service request of the PSU. This is represented on all 4 alternatives by arrow 1<sub>a(__x,c__)</sub> and arrow 1<sub>b(__s__)</sub>, where by:.
- __c__ is the session cookie between the FinTechUI and the FinTechApi. This is, we assume that the FinTechApi identified the PSU with (psu-id@fintech).
- __x__ is the XSRF-Token parameter used to protect the cookie again XSRF.
- __s__ is the state parameter generated by the FinTech and intended to be used to protect any future redirect cookie.

### Step-2 Redirect to TPP
As the TPP maintains and manages consents previously provided by the PSU, the TPP will check for the existence of a suitable consent upon reception of the banking service request. IF there is no consent, the instruct the FinTech to redirect the PSU to the TPP consent authorization interface. This procedure contains following steps:

#### Initiation: Arrow 2<sub>a(__s,i__)</sub>
This step is necessary to avoid associating critical parameter with the redirect request. This initiation request carries
- __i__: an identifier of the redirect session. This same identifier will be associated to the redirect url used to send the PSU to the consent authorization interface of the TPP.
- __s__ is the state parameter generated by the FinTech and intended to be used to protect any future redirect cookie. This state parameter will be associated with the back redirect url by the TPP while redirecting the PSU to the UI of the FinTech.

This redirect step will contain any additional information associated with the service request to be authorized.

#### Instruction to Redirect: Arrow 2<sub>b(__s,i__)</sub>
In this step, the TPP instructs the TPP to redirect the PSU to the consent authorization interface of the TPP. This instruction contains 2 main information of interest:
- __i__: the identifier of the redirect session. This same identifier will be associated to the redirect url used to send the PSU to the consent authorization interface of the TPP.
- __s__ is the state to be used by the FinTech to protect the redirect cookie.

#### Instruction to Redirect: Arrow 2<sub>c(__r,i__)</sub>
In this step, the FinTechApi instructs the FinTechUI to redirect the PSU to the consent authorization interface of the TPP. As we discussed above, the way we redirect we strongly depend on the nature of the user interface. For simplicity, we will assume that the FinTechUI has access to the redirect response and can protect access to the RedirectCookie.

This Arrow has following information of interest: 
- __r__: This cookie is to be stored by the FinTechUI and returned to the FinTechApi with the back redirect request.
- __i__: the identifier of the redirect session. Will be part of the redirect url. Depending on the client technology, this can be either a path or a query parameter. It is safer to make this path parameter as this wont be striped away by some user agent container. See [atomic-http-redirect-handling](https://fetch.spec.whatwg.org/#atomic-http-redirect-handling). 

#### Instruction to Redirect: Arrows 2<sub>d(__i__)</sub>, 2<sub>e(__i__)</sub>
For simplicity, we assumed that the TPP-UI (EMbeddedConsentUI) is running in a web browser. This is, the FinTechUI instructs it container (either the web browser or the operating environment) to open the TPP-UI on the user device.

### Step-3 Authorize Consent

#### Authorize Service Request: Arrow 3<sub>a..y</sub>
The previous step is followed by the TPP interacting with the PSU to authorize the service requested. The TPP starts by using the redirect identifier to retrieve request details. Assuming we are dealing with an embedded SCA, the TPP will prepare and display service details to the PSU and interact with the PSU to allow the PSU to __authorize the service execution__ (consent). In the process of the authorization, the PSU is identified as __psu-id@tpp__ that in our case is equivalent to __psu-id@aspsp__ as the consent occurred over the embedded TPP interface. 

#### Confirmation Code: Arrow 3<sub>z(__a__)</sub>
Once the service request is authorized, the ASPSP online banking interface can return an authorization code to the TPP (__a__). This authorization code must be brought by the TPP in a subsequent step to confirm execution of the service request.

### Step-4 Redirect to FinTech
Once authorization is performed, the TPP will have to return control to the FinTech by redirecting the the PSU to the FinTechUI. This redirection step is essential as we have to:
- make sure control is given back to the same PSU that authorized the consent on the TPP interface (__psu-id@tpp__),
- make sure that PSU is the same natural person that originally initiated the service request on the FinTech interface (__psu-id@fintech__).

This is what we call the identity equivalence __psu-id@aspsp ==> psu-id@tpp ==> psu-id@fintech__. Upon verification of this identity equivalence, a confirmation call can be sent to the TPP banking interface to trigger execution of the service request.

#### Instruction to Redirect: Arrow 4<sub>a(__s,a__)</sub>
In the first step, the TPP authorization interface instruct the user agent to redirect the PSU back to the FinTechUI. In all 4 use cases, the redirect request carries both:
- __s__: state parameter used to validate the RedirectCookie at the FinTechApi interface
- __a__: the authorization code needed to issue a confirmation call to the TPP banking interface.

#### Alt-1: API Redirect and In-Browser
The next step depends strongly on whether the back redirect is addressed to the FinTechApi or to the FinTechUI. If the back redirect is addressed to the FinTechApi, this request will have to be performed by the UserAgent hosting the TPP-UI. Independent on the nature of the FinTechUI, we want the TppBankingApi to received the RedirectCookie returned with the original RedirectResponse. This will be the case if the FinTechUi is run by the same web browser as the EmbeddedConsentUI. This is why we have the arrow 4<sub>b(__s,a,r__)</sub>. This is why this arrow carries:
- __s__: state parameter used to validate the RedirectCookie at the FinTechApi interface
- __a__: the authorization code needed to issue a confirmation call to the TPP banking interface.
- __r__: the redirect cookie, sent to the FinTechApi in this step because it is held by the common browser instance.

In this alternative, the FinTechApi will have to use an additional redirect step to present the FinTechUI to the PSU. This is represented by the arrows 4<sub>c(__x,c__)</sub>, 4<sub>d(__x__)</sub>, 4<sub>e(__x__)</sub> and 4<sub>f(__x,c__)</sub>

Upon receiving the request 4<sub>b(__s,a,r__)</sub>, the FinTechApi uses the state __s__ to validate the contained RedirectCookie and creates a new session cookie __c__ and corresponding XSRF-Token __x__ that are both used to launch the FinTechUI using arrow 4<sub>c(__x,c__)</sub>. The web browser receiving the launch instruction will store the new session cookie, and use the provided redirect url containing the state parameter to load FinTechUi. The FinTechUI will finally parse the XSRF-Parameter from the loading request and use it to issue the confirmation call to the FinTechApi: 4<sub>f(__x,c__)</sub>.  
 
#### Alt-2: API Redirect and Browser to NativeApp
If redirecting back to the FinTechApi the FinTechUI holding the cookie is a native application, there will be no automatic transfer of the RedirectCookie to the FinTechApi. In this case the arrow 4<sub>b(__s,a__)</sub> will not have __r__. This arrow will carry only:
- __s__: state parameter used to validate the RedirectCookie at the FinTechApi interface
- __a__: the authorization code needed to issue a confirmation call to the TPP banking interface.

In this alternative, the FinTechApi will have to use an additional redirect step to launch the FinTechUI native application to the PSU device. This is represented by the arrows 4<sub>c..e(__x,c__)</sub>. The launch URL will have to contain both __s__ and __a__. Remark that there is no way to protect the integrity of these parameters.

Using arrow 4<sub>e(__s,a__)</sub> will deep link into the FinTechUi App that will parse those parameters and use them to send the confirmation request to the FinTechApi using arrow 4<sub>f(__s,a,r__)</sub> where:
- __s__: state parameter used to validate the RedirectCookie at the FinTechApi interface
- __a__: the authorization code needed to issue a confirmation call to the TPP banking interface.
- __r__: the redirect cookie, stored by the FinTechUI while processing the original redirect to the TPP.

#### Alt-3: UI Redirect and In-Browser
Like described in Alt-1, the back redirect request is a response to the web browser running the TPP-UI. This browser uses the arrow 4<sub>b,c(__s,a__)</sub> to load the UI-Files (off course without the RedirectCookie). UI-Files will parse both __s__ and __a__ from the url and use them in arrow 4<sub>d(__s,a,r__)</sub> to issue the confirmation call to the FinTechApi.

#### Alt-4: UI Redirect and Browser to NativeApp
As launcher of FinTechUI NativeApp is on the user device, the browser running the TPP-UI will use the the arrow 4<sub>b(__s,a__)</sub> to instruct the user device to start the FinTechUI. The user device will use the arrow 4<sub>c(__s,a__)</sub> to deep link into the FinTechUi App that will parse those parameters and use them to send the confirmation request to the FinTechApi using arrow 4<sub>d(__s,a,r__)</sub> where:
- __s__: state parameter used to validate the RedirectCookie at the FinTechApi interface
- __a__: the authorization code needed to issue a confirmation call to the TPP banking interface.
- __r__: the redirect cookie, stored by the FinTechUI while processing the original redirect to the TPP.

#### API vs. UI-Redirect
The redirect alternative to choose API (Alt-1 & Alt-2) vs. UI (Alt-3 & Alt-4) is dependent on the architecture of the final environment.
- Selecting the API redirect approach :
  - (-) might require the FinTechApi to turn off CORS
  - (-) will require two Endpoints at the FinTechAPI, the afterRedirect(4<sub>b(__s,a,r__) or 4<sub>d(__s,a__)) and the afterUIReload(4<sub>f(__x,c__) or 4<sub>f(__s,a,r__)). And like we see those EndPoints will have to be dealt with differently depending on whether call back is in Alt-1 or Alt-2.  
- Selecting the UI approach :
  - (-) might require the FinTechUI to turn off CORS. Although we are more confortable turning off CORS on static content files than doing this on the API interface.
  - (+) will simplify the FinTechApi interface by providing a single EndPoint afterRedirect(4<sub>d(__s,a,r__)) for both Alt-3 and Alt-4.

#### Confirm Service Execution: Arrows 4<sub>g(__a,i__)</sub>, 4<sub>z(__a__)</sub>
The confirmation step is initiated after the FinTech verifies the equivalence between psu-id@tpp and psu-id@fintech. This is executed by using arrow 4<sub>g(__a,i__)</sub> to forward a confirmation call to the TPP banking interface that will in turn use arrow 4<sub>z(__a__)</sub> to confirm execution of the service with the open banking interface of the ASPSP.
 
