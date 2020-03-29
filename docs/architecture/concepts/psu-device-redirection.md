# User Agent Redirection

## Abstract
Platform business is generally characterized by multiple applications that collaborate with each other by the mean of sharing data and functionality to provide more valuable services. The sharing of user identity is a service that is being intensively used by online service providers to reduce the quantity of accounts to be managed by a single user. A PFM (Personal Finance Manager) application will load user banking transactions and analyze them to help the user have a better insight on his financial activities. As user banking transaction data are held by the bank, data sharing will occur by the mean of the user allowing his bank to share his banking transactions with the PFM provider.

The raise of online business platforms is happening from all corners of the world. In some regions, platform business is driven by regulators mandating custodians of user data to share these at the user's will with third parties. In some other regions, platform business initiative is driven by market participants. In some regions, data sharing initiatives are limited to financial or even only payment account data, while in some other regions, data sharing initiatives extends their scope to many other domains like telecommunication, health, utilities etc...

Although sharing data has always been practiced in the business environment (see EBICS), the novelty here is the capacity of ad-hoc online connectivity of business, that strive to enable data sharing without time expensive preceding offline authorization steps at the agency location of the custodian. In the case of EBICS, the bank account owner (let's call him data owner) will have to go to the bank (a.k.a custodian) and explicitly give an authorization to release his data to a designated third party. In a world dominated by real time online access to applications, authorization steps want to be done on the go and in real time. This is why the online platform business community leverages __User Agent Redirection__ to allow for real time online connectivity and interaction between the user and many participating service providers.

The first intensive use of __User Agent Redirection__ was battle tested with the adoption of oAuth2 and OpenID Connect. For those use cases, the primary purpose of redirection is the sharing of identity and the authorization to share identity related information. In the online platform business domain, __User Agent Redirection__ is being used to manage authorization and sharing of functionality far beyond identity, like the delegation of fundamental personal authoritative permissions such as allowing a third party to initiate a payment from the user's bank account.

Independent on whether the __User Agent Redirection__ is being used for online real time identity sharing or for more advanced authorization frameworks, it is essential to make sure that the natural person being redirected from one application to another to express his will __can not be impersonated__ by any mean (technical or social engineering). 

### Common Terms
Let's call:
- the __SOURCE_APP :__ the remote application that redirects the user to another application (the __TARGET_APP__),
- the __SOURCE_UI :__ the user agent used by the user to access the source application,
- the __TARGET_UI :__ the user agent used by the user to access the target application,
- an __AUTOMATIC_REDIRECTION :__ a situation in which the __SOURCE_UI__ __automatically__ presents the __TARGET_UI__ to the user. This generally happens when __SOURCE_UI__ and __TARGET_UI__ are on the same user device.
- a __MANUAL_REDIRECTION :__ a situation in which the __SOURCE_UI__ __instructs__ the user to open the __TARGET_UI__. Even if for example a push notification is used to help the user activate the __TARGET_UI__, we consider it a manual redirection.
- an __AUTOMATIC_TRSP_MESSAGE :__ a message automatically transported from the __SOURCE_APP__ to the __TARGET_APP__ without explicit intervention of the user. E.g.:
  - A query parameter attached to the redirection URL;
  - An information carried by the push notification sent to activate the TARGET_UI.
- a __MANUAL_TRSP_MESSAGE :__ a message explicitly collected from the __SOURCE_UI__ and input into the __TARGET_UI__ by the user. E.g.:
  - A user using the __TARGET_UI__ to scan a QR_CODE;
  - A user sharing an Image from the __SOURCE_UI__ to the __TARGET_UI__;
  - A user typing a number in the __TARGET_UI__.

### Identity Equivalence
We want to make sure the natural person controlling access to __SOURCE_UI__ (let's call him __psu-id@source__) is the same natural person controlling access to the __TARGET_UI__ (let's call him __psu-id@target__). We will call this verification the __identity equivalence__. This identity equivalence seats at the core of the integrity of __User Agent Redirection__ based online business interactions between providers. 

## Redirection Based Sharing of Identity
An online business service provider generally implements functionality using clients and server side applications. It doesn't matter if biggest part of the application is run on the client side (UI) or on the server side (APP), both sides are generally found in almost all online business applications. This is, the typical design of an online business application looks like:

![Client Server Layout](../../img/redir/client-server.png)

Each application (APP) presents a user agent to the user. The identifier user@app-1 is used by APP-1 to manage the session between the UI-1 and APP-1 (resp. user@app-1 for UI-2 and APP-2). The following picture shows the delegation of identity management to an identity provider.

![IDP Layout](../../img/redir/idp-client-server.png)

In this case, APP-1 and APP-2 delegate authentication to the same identity provider and thus share the same identity (user@idp). Each delegation process generally involves redirection. In order to make sure that redirection based delegation process will not lead to an impersonation of the user, well defined protocols like oAuth2 are adopted by a wide part of the market. Nevertheless, even the most secure identity provider will not prevent badly connected applications (relying party) to offer space for the impersonation of the end user. The following picture display the classical oAuth2 workflow.

![oAuth2 Flow](../../img/redir/oauth2-flow.png)

The workflow displayed above shows an identity sharing process bases on oAuth2. Nothing states that both UI applications are on the same user device. In the rest of this document, we will assume __AUTOMATIC_REDIRECTION__ when both UI-RP and UI-IDP are on the same device and __MANUAL_REDIRECTION__ when both are not on the same device.

### Automatic Redirection to the Identity Provider
This is a situation in which the __SOURCE_UI__ __automatically__ presents the __TARGET_UI__ to the user. This generally happens when both __SOURCE_UI__ and __TARGET_UI__ are on the same user device, even if they are not in the same web browser. There are many technologies used to enable automatic redirection on devices.
- HTTP redirect (30x) are used to automatically instruct web browser to redirect the user to another page. It is nevertheless worth to mention that this only works when the originating request sent to the APP by the UI is not an XHR-Request. In case of XHR-Request, redirection has to be handled by the JavaScript Framework providing the XHR functionality.
- __iOS Universal Link__ and __android App Links__ are used by the corresponding operating systems to automatically launch the TARGET_UI.
- __custom URI schemes__ are also used to instruct the device to launch a dedicated __TARGET_UI__ application. Whereby Universal Links resp. (App Links) are a better way to proceed as the request will be forwarded to the APP if the UI is not installed on the user device yet. 

The authorization flow starts by the mean of the relying party (RP) redirecting the user to the identity provider. As we see on the picture above, the source of the redirection would like to transport some information to the target of the redirection. The way information is transported highly depends on the nature of the redirection.

In the case of __AUTOMATIC_REDIRECTION__, information are transported as URL parameters. We have to be aware that there is no way to protect information transported from a UI application to another UI application on the user device. Any of those parameters can be modified by malicious program code running on the user device. In order to prevent modification of redirected parameter information, oAuth can be extended with an initiation step that will simplify implementation and increase robustness of the oAuth protocol. This is the approach suggested in [draft-ietf-oauth-par-00](https://tools.ietf.org/html/draft-ietf-oauth-par-00).

If we do not have an initialization step and as we know there is no way to control the integrity of an information passed by one UI application to another one on a user device, some technical protection means must be used to make sure the control will not be returned to the wrong UI application. The type of protection used depends on the nature and the purpose of the information: 

- The __redirect_uri__: is used to instruct the identity provider (IDP) on where to send the user back after authentication and authorization. If this information is changed on the way to the IDP, there is a risk the IDP redirects control back to the wrong user agent. If the flow is not using an initialization step, the pre-configured URL-Template will have to be registered by the relying party (RP) with the IDP before any interaction happens. Many technology initiatives propose way of preventing this sort of impersonation. Among other: PKCE (see [RFC7636](https://tools.ietf.org/html/rfc7636)), the use of a __RedirectCookie__ to hold a state inside the UI-RP, so that APP-RP can guaranty that control has been given back to original UI-RP instance controlled by user@idp and not to another UI-RP controlled by attacker@idp.

- The __state__: is generally used to hold the state of user agent of the relying party (UI-RP). The state can be used to retrieve a __XSRF__ token stored by the UI-RP and used to prevent unauthorized reuse of the RedirectCookie.

### Manual Redirection to the Identity Provider
Let assume the user agent of the identity provider (UI-IDP) is a NativeApp and the server application of the relying party (APP-RP) suspects the identity of the user is (user@idp), then the redirection can occur by the mean of APP-RP instructing APP-IDP to send a push notification to UI-IDP. The user will then manually touch the notification message to open UI-IDP and identify with APP-IDP.

This initiation of a __MANUAL_REDIRECTION__ can be perceived as an initiation step as well. Despite the case of a automatic redirection, the presence of a suspected user@idp is necessary to discover the device on which to proceed with the authorization.

A manual opening of the UI-IDP indicates by no mean that the user is in control of both UI-RP and UI-IDP. In order to make sure the user controls both environments, it is necessary to have the user manually collect some information displayed by the UI-RP and enter them in the UI-IDP. We call this __Device Linking__. The way the display and collection process works depends on the nature of both UI-RP and UI-IDP.
- If for example the UI-RP is a browser application running on a desktop computer and the UI-IDP is a NativeApp running on a mobile phone, the UI-IDP can provide tools for __scanning a QR_CODE__ displayed by the UI-RP.
- The UI-IDP might as well just request the user to enter a sequence of digit displayed by the UI-RP.
Again, __linking devices__ in manual redirection case is essential to make sure the same user is in control of both UI-IDP and UI-RP.

### Automatic Return of Control to the Relying Party
The user uses UI-IDP to authenticate with the APP-IDP and there is a session between UI-IDP and APP-IDP. The IDP must return control to the relying party so RP can assert user identity and proceed with the service request.

In an __AUTOMATIC_REDIRECTION__ case, where UI-IDP and UI-RP are on the same device, a technical redirect can be used to send control back to RP. As describe above HTPP 30x, Universal Link, App Link and custom URI are all means used to automatically pass control from one application to another one. As these technical methods are all URL based, URL parameters can be used to transport information to the __TARGET_UI__. We also display above that there is no way to control the integrity of an information passed by one application to another one on a user device. The IDP generally sends two information back to the RP:

- The __state__ parameter sent back as received by the IDP and used to validate that control was sent back to the UI instance that initiated the authentication flow.
- The __code__ parameter sent by the IDP and used by the RP to retrieve the final authorization token.

### Manual Return of Control to the Relying Party
A manual return of control to the relying party (RP) can happen in the background by having the APP-IDP produce the token and send it in a back channel to the RP, and makes it the responsibility of the APP-RP to activate the UI-RP, as there an active session between APP-RP and UI-RP.

## Securing Automatic Redirection
An automatic redirection from the relying party (APP-RP) to the identity provider (APP-IDP) is associated with a lot of risks.

### Session Fixation Attack
A well known attack is having a malicious user called Bob start a session with the relying party (UI-RP-Bob -> APP-RP) and trick Alice to use the redirect url to start a authorization session with the identity provider (UI-IDP-Alice -> APP-IDP).

Some framework will allow UI-RP-Bob to simply poll for completion of the authorization request and proceed forward when Alice has provided her consent (UI-IDP-Alice -consent-> APP-IDP). This is an obvious flaw in the authorization flow.

In order to make sure authorization being performed by Alice (UI-IDP-Alice -> APP-IDP) was really initiated by Alice (UI-RP-Alice -> APP-RP) and not by Bob (not[UI-RP-Bob -> APP-RP]), APP-IDP has to auto redirects control to APP-RP through Alice UIs (APP-IDP -> UI-IDP-Alice -> UI-RP-Alice -> APP-RP). This redirection must carry an authorization code that is used by the relying party to collect the token from the identity provider in the back channel( APP-RP -authCode-> APP-IDP). 

### Open Redirector
In order for Bob to gain access to the authorization code, UI-RP-Bob needs to have modified the redirect_uri of the authorization request to include an open redirector hack (see [CWE-601](https://cwe.mitre.org/data/definitions/601.html)).

Preventing Bob from gaining access to the authorization code boils down to making sure the redirect_uri processed by the IDP-APP in sanitized. This requires the effort of both, the IDP-APP and the RP-APP:
- If RP-APP can use an initiation step (see [draft-ietf-oauth-par-00](https://tools.ietf.org/html/draft-ietf-oauth-par-00)), it can make sure the redirect_uri is sent to the APP-IDP via the back channel initiation request and does not contain any malicious query parameter controlled by the UI.
- If the redirect_uri has to be transported to the IDP-APP through the UI's, there is no way to prevent UI-RP-Bob from manipulating it. In this case, APP-IDP will have to :
  - check the redirect_uri provided with the authorization request against open redirect patterns before proceeding with the authorization, and 
  - validate that the redirect_uri provided with the authorization request matches the redirect_uri provided with the token request performed in the back channel before issuing the token.

### Browser Interception Attack
In order to make sure authorization process was started by UI-RP-Alice, APP-IDP will return control to APP-RP by redirecting the authorization response to APP-RP through Alice UIs (APP-IDP -> UI-IDP-Alice -> UI-RP-Alice -> APP-RP). The authorization code associated with this back redirect is used by the APP-RP to recover the authentication token from APP-IDP through the back channel.

Although "APP-IDP -> UI-IDP-Alice" and "UI-RP-Alice -> APP-RP" are protected by TLS, authorization code can be intercepted on the link "UI-IDP-Alice -> UI-RP-Alice". Another application registered with for the URL on Allice device (attack well explained in [PKCE RFC7636](https://tools.ietf.org/html/rfc7636)). Authorization code can be intercepted on Alice device by other means as we can not control the state of a client device. 

##### User Agent Binding
While initiating redirection to APP-IDP, APP-RP can set a RedirectCookie with UI-RP-Alice (including the associated XSRF-Token). This RedirectCookie is physically bound to the user agent UI-RP-Alice.
- If the redirection is performed using HTTP-302/-303, the XSRF protecting the RedirectCookie must be stored in the state parameter associated with the redirect_uri. 
- If the redirection is performed using HTTP-202, the XSRF parameter can be held in the local storage of UI-RP-Alice before forwarding control to UI-IDP-Alice.

While redirecting back from APP-IDP (APP-IDP -> UI-IDP-Alice -> UI-RP-Alice -> APP-RP), APP-RP will only accept the redirect code if UI-RP-? provides the original RedirectCookie (And corresponding XSRF parameter). With this approach intercepting the authorization code will not help, as UI-RP-Bob will not have access to information stored by UI-RP-Alice.

Note that PKCE cannot be used to solve this problem.

##### Malicious Relying party 
Bob could install a relying party (APP-RP-Bob) that can impersonate the original APP-RP to request token on it's behaves. Recall that must relying party will be deployed by company with not enough experience in application security. So stealing static configuration information like the client-secret wont be a major issue for well trained attackers.

In order to prevent a malicious RP (APP-RP-Bob) from using an intercepted authorization code to obtain the token, IDP and RP can be require to implement server side [PKCE RFC7636](https://tools.ietf.org/html/rfc7636). The advantage of PKCE over classical RP authentication information like the "client-secret" are static while generated code_verifier are dynamic and vary with each authorization request. 


## Beside Identity Sharing
The oAuth framework defines many models of interaction between IDP and RPs. Beside the redirection base authorization flows (implicit and authorization code), the password grant flow is one that allows a relying party to collect user credentials and forward them to the identity provider. This flow generally assumes the RP is trusted and will not leak transported user credentials.

With a password grant flow, there is no need to fear any impersonation of the user, as the RP that collects user credential maintains a session with the user.
 
### Extending Identity Sharing to Advanced Use Cases
The experience realized with sharing identity in online business interaction has lead to an attempt to share even more data and functionality using the same patterns. Many online business platform initiatives are reusing the experience made over the last decade with oAuth, OpenId Connect and other __Identity Sharing__ schemes to define more advanced authorization frameworks that enable the sharing of other data and services on behalf of an end user. In most OpenBanking approaches evolving out there, a banking customer can authorize the custodian of his bank account (the bank) to open some functionalities to third parties. This way a third party provider of online services might pull the list of transactions of the end user's bank account or even initiate a payment on behalf of that end user.

The way a banking user give his consent to the bank is very similar to the way an online user allows the IDP to share his identity with a relying party. As these authorization frameworks are reaching the banking industry, there is an imminent need of providing a very clear understanding of how sharing processes work, so we can reduce the number of erroneous implementation on the market. Recall there will be a bigger incentive for malicious parties to invest effort into exploiting weaknesses associated with redirection as this gets use in open banking.
