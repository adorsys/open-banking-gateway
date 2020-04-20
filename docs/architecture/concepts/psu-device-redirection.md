# Open Banking and the Complexity of User Agent Redirection

## Abstract

Most Open Banking interaction patterns rely on user agent redirection (as seen in the world of identity management) to implement sharing of banking data and banking services.

In some regions, sharing of online user banking access is driven by regulators mandating custodians (a.k.a. ASPSP or banks) to share access to user banking data at user's will with third party providers (See European PSD2). In some other regions, data sharing initiatives extend their scope to many other business domains such as telecommunication, health, and utilities (See Australian CDR).

Although sharing data has always been practiced in banking business (see EBICS), the novelty of Open Banking lies within the capacity of ad-hoc online connectivity of business, that strives to enable data sharing without preceding time expensive offline authorization steps at the agency location of the custodian. In the case of EBICS, the bank account owner will use an offline channel to have his bank (custodian) explicitly give authorization to provide access to a designated third party. In a world dominated by real time online access to applications, authorization steps need to be taken right away and on the go. This is why the online platform business community leverages __User Agent Redirection__ to allow for real time online connectivity and interaction between the user and many participating service providers.

It is explained why another approach, __Embedded SCA__, is more suitable and more secure for sharing identities at least between regulated and supervised financial service providers, e.g. within Open Banking in Europe.

### The Challenge

Independent of whether __User Agent Redirection__ is being used for online real time identity sharing, or for more advanced authorization frameworks, it is imperative that the natural person being redirected from one application to another can express his or her free will without threat of __impersonation__ by any means (technical, social engineering, etc.).

The use of __User Agent Redirection__ for sharing access to banking services will increase the malicious party incentive to invest more effort into exploiting loopholes. Among malicious parties we account:
- Attackers having an interest in executing fraudulent banking transactions;
- Companies financing malicious operations with the intention of illegally accessing banking transactions of competitors for industrial espionage;
- Government driven cyber-attack with the purpose of gaining information on the financial behavior of citizens of the target country.

### Complexity of User Agent Redirection

The purpose of this work is to provide insight into the complexity associated with a secure implementation of __User Agent Redirection__. Instead of narrating some proprietary use cases in Open Banking, we want to show that even the more mature, better known and intensively utilized case of identity provisioning (oAuth2) is too complex and not yet well enough thought out.

The current state of implementation for Open Banking shows that neither financial institutions (known as ASPSP), nor third party providers (AISP, PISP, PIISP), are experienced enough to implement and operate solid, tamper proof __User Agent Redirection__ based Open Banking sharing processes.

## Highlighting User Agent Redirection

### Common Terms
Let's call :
- __APP-RP :__ the server application of a relying party that redirects user to an identity provider,
- __UI-RP :__ the UI application utilized to access the server application of the relying party,
- __APP-IDP :__ the server application providing the identity service,
- __UI-IDP :__ the UI application utilized to access the identity provider,
- __AUTOMATIC_REDIRECTION :__ a situation in which UI-RP __automatically__ presents UI-IDP to the user. This generally happens when UI-RP and UI-IDP are on the same user device,
- __MANUAL_REDIRECTION :__ a situation in which UI-RP __instructs__ the user to open UI-IDP. Even if a push notification is used to help the user activate the UI-IDP.

### Redirection Based Sharing of Identity
An online business service provider generally uses client and server side applications to implement functionality. Whether or not the biggest part of the application is run on the client side (UI-?) or server side (APP-?), both sides are generally found in almost all online business applications. The typical design of an online business application looks like:

![Client Server Layout](../../img/redir/client-server.png)

Each server application (APP-?) presents a user agent (UI-?) to the end user. The identifier user@app-1 is utilized by APP-1 to manage the session between UI-1 and APP-1 (resp. user@app-2 for UI-2 and APP-2). The following picture shows the delegation of identity management to an identity provider.

![IDP Layout](../../img/redir/idp-client-server.png)

In this case, APP-1 and APP-2 delegate authentication to the same identity provider and thus share the same identity (user@idp). Each delegation process involves redirection. In order to make sure that redirection based delegation process will not lead to user impersonation, a wide part of the market has adopted the oAuth2 protocol. Nevertheless, even the most secure identity providers will not prevent poorly connected applications (relying parties) from offering space for impersonation of the end user. The next picture displays a classical oAuth2 authorization code workflow, showing how even oAuth2, if not well implemented, does not protect the end user from identity fraud.

![oAuth2 Flow](../../img/redir/oauth2-flow.png)

The workflow displayed above shows an identity sharing process based on oAuth2. Nothing states that both UI applications are on the same user device. In the rest of this document, we will assume __AUTOMATIC_REDIRECTION__ when both UI-RP and UI-IDP are on the same device, and __MANUAL_REDIRECTION__ when both are not on the same device.

### Automatic Redirection to the Identity Provider
This is a situation in which UI-RP __automatically__ presents UI-IDP to the user. It generally happens when both UI-RP and UI-IDP are on the same user device, even if they aren’t in the same web browser instance. There are multiple technologies available to enable automatic redirection on devices.

- HTTP redirects (HTTP-302/-303) are utilized to automatically instruct the web browser to redirect the user to another page.
- HTTP accept (HTTP-202) can also be used with native mobile applications and browser based single page applications to instruct UI-RP to redirect the user to UI-IDP. 
- __iOS Universal Link__ and __android App Links__ are used by the corresponding operating systems to automatically launch the target UI (either UI-RP or UI-IDP).

 The authorization flow starts with the relying party (RP) wanting to redirect control to the identity provider. As  seen above, the source of redirection (APP-RP arrow 1) sends some information to the target (APP-IDP) of the redirection. The way information is transported highly depends on the nature of the redirection.

In the case of __AUTOMATIC_REDIRECTION__, information transport happens in the form of  URL query parameters. We must be aware that there is no way to protect information transported from a UI application to another UI application on a user device. Any of those parameters can be modified by a malicious program code running on the user device. In order to prevent modification of redirected parameters, oAuth2 must be extended with an initiation step that simplifies implementation and increases robustness of the oAuth2 protocol. This is the approach suggested in [draft-ietf-oauth-par-00](https://tools.ietf.org/html/draft-ietf-oauth-par-00).

If we do not have an initialization step - and as we know there is no way to control the integrity of an information passed by one UI application to another one on a user device - some technical protection means must be used to make sure that after authorization, APP-IDP will not return control to the wrong UI application (because of modified redirect_uri). The type of protection to be implemented depends on the nature and the purpose of the information transported: 

- The __redirect_uri__: Used to instruct the identity provider (IDP) on where to send the user back after authentication and authorization is done. If this information is changed on the way to the IDP, there is a risk that the IDP redirects control back to the wrong user agent. If the flow is not using an initialization step, the APP-RP will have to register the pre-configured URL-Template with APP-IDP before any redirection happens. More related security issues are described in 
[draft-ietf-oauth-security-topics-14](https://tools.ietf.org/html/draft-ietf-oauth-security-topics-14).

- The __state__: Generally used to hold the state of UI-RP during a redirection process. The state can be used by UI-RP to help recover persistent information associated with the redirect process.

### Manual Redirection to UI-IDP
Let's assume UI-IDP is a NativeApp, APP-RP suspects the identity of the user is (user@idp), then the redirection can occur by means of APP-RP instructing APP-IDP to send a push notification to UI-IDP. The user will then manually touch the notification message to open UI-IDP and identify with APP-IDP.

This initiation of a __MANUAL_REDIRECTION__ can be perceived as an initiation step as well. In contrast to automatic redirection, the presence of a suspected user@idp is necessary to discover the device on which to proceed with the authorization.

A manual opening of UI-IDP does not indicate that the user is in control of both UI-RP and UI-IDP. In order to make sure the user controls both environments, it is necessary to have the user manually collect some information displayed by UI-RP and enter them in UI-IDP. We call this __Device Linking__. The way the information display and collection process works depends on the nature of both UI-RP and UI-IDP.
- If for example UI-RP is a browser application running on a desktop computer and UI-IDP is a NativeApp running on a mobile phone, UI-IDP can provide tools for __scanning a QR_CODE__ displayed by UI-RP.
-  UI-IDP might as well just request the user to enter a sequence of digits displayed by UI-RP.
Again, __linking devices__ in manual redirection cases is essential to make sure the same user is in control of both UI-IDP and UI-RP.

### Automatic Return of Control to the Relying Party
The user utilizes UI-IDP to authenticate with APP-IDP and there is a session between UI-IDP and APP-IDP. APP-IDP must return control to the APP-RP in order for APP-RP to assert user identity (token) and proceed with the service request.

In an __AUTOMATIC_REDIRECTION__ case, where UI-IDP and UI-RP are on the same device, a technical redirect can be used to send control back to UI-RP. As described above HTPP 302/303/202, Universal Links and App Links are all means used to automatically pass control from one application to another one. As these technical methods are all URL based, URL parameters can be used to transport information from UI-IDP to UI-RP. We also mention above that there is no way to control the integrity of an information passed by one UI application to another one on a user device. The IDP generally sends two information back to RP:
- The __state__ parameter is sent back as received by the IDP and is used by UI-RP to validate that control was sent back to the very UI instance that initiated the authentication flow.
- The __code__ parameter is sent by the IDP and used by APP-RP to retrieve the authorization token.

The next problem associated with an automatic redirection to UI-RP is that, if UI-IDP is a native application and UI-RP is a browser application, control might be returned to the wrong browser instance. This generally happens when the user did not start UI-RP on the system browser. 

### Manual Return of Control to the Relying Party
A manual return of control to APP-RP can happen in the back channel by having APP-IDP produce the token and send it in the back channel to APP-RP, making it the responsibility of APP-RP to activate UI-RP, as there is an active session between APP-RP and UI-RP.

## Securing Automatic Redirection
An automatic redirection from APP-RP to APP-IDP for the purpose of collecting user authorization is associated with a lot of risks.

For better illustration, let's call:
- Alice: the honest owner of an IDP account, 
- Bob: the malicious person trying to gain control of Alice’s identity,
- UI-RP-Alice: the user agent utilized by Alice to access APP-RP,
- UI-IDP-Alice: the user agent utilized by Alice to access APP-IDP,
- UI-RP-Bob: The user agent utilized by Bob to access APP-RP.

Below is a non-exhaustive list of topics of concern while using automatic redirection to authorize Alice. A more extensive list can be found in [draft-ietf-oauth-security-topics-14](https://tools.ietf.org/html/draft-ietf-oauth-security-topics-14).

### Session Fixation
A well known attack is having Bob start a session with the relying party (UI-RP-Bob -> APP-RP) and trick Alice into using the redirect url to start an authorization session with the identity provider (UI-IDP-Alice -> APP-IDP).

Some frameworks will allow UI-RP-Bob to simply poll for completion of an authorization request and proceed forward when Alice has provided her consent (UI-IDP-Alice -consent-> APP-IDP). This highlights an obvious flaw in the authorization flow as Bob will gain control of the session as soon as Alice completes her authorization with the IDP.

In order to make sure authorization being performed by Alice (UI-IDP-Alice -> APP-IDP) was really initiated by Alice (UI-RP-Alice -> APP-RP), and not by Bob (UI-RP-Bob -> APP-RP), APP-IDP has to auto redirect control to APP-RP through Alice UIs (APP-IDP -> UI-IDP-Alice -> UI-RP-Alice -> APP-RP). Such a redirection must carry an authorization code that is used by the relying party to collect the token from the identity provider in the back channel( APP-RP -authCode-> APP-IDP). This logical mistake still exists in some Open Banking protocol designs See [Cross Browser Payment Initiation Attack] (https://bitbucket.org/openid/fapi/src/master/TR-Cross_browser_payment_initiation_attack.md).

### Open Redirector
In order for Bob to gain access to the authorization code, UI-RP-Bob needs to have modified the redirect_uri of the authorization request to include an open redirector hack (see [CWE-601](https://cwe.mitre.org/data/definitions/601.html)).

Preventing Bob from gaining access to the authorization code boils down to making sure the redirect_uri processed by APP-IDP is sanitized. This requires the effort of both, APP-IDP and APP-RP:
- If APP-RP can use an initiation step, see [draft-ietf-oauth-par-00](https://tools.ietf.org/html/draft-ietf-oauth-par-00), it can make sure the redirect_uri is sent to APP-IDP via a back channel initiation request and does not contain any malicious query parameter controlled by the UI.
- If the redirect_uri has to be transported to the APP-IDP through the UI's, there is no way to prevent UI-RP-Bob from manipulating it. In this case, APP-IDP will have to :
  - check the redirect_uri provided with the authorization request against open redirect patterns before proceeding with the authorization, and 
  - validate that the redirect_uri provided with the authorization request matches the redirect_uri provided with the back channel token request before issuing the token.

### Browser Interception Attack
In order to make sure authorization process was started by UI-RP-Alice, APP-IDP will return control to APP-RP by redirecting the authorization response to APP-RP through Alice UIs (APP-IDP -> UI-IDP-Alice -> UI-RP-Alice -> APP-RP). The authorization code associated with this back redirect is used by APP-RP to recover the authentication token from APP-IDP through the back channel.

Although "APP-IDP -> UI-IDP-Alice" and "UI-RP-Alice -> APP-RP" are protected by TLS, authorization code can be intercepted on the connection "UI-IDP-Alice -> UI-RP-Alice". This can be done by another application registered for the URL of UI-RP on Allice device (attack well explained in [PKCE RFC7636](https://tools.ietf.org/html/rfc7636)). Authorization code can be intercepted on Alice’s device by other means as we can not control the state of a client device. 

### Binding User Agent (Counter Measure)
While initiating redirection to APP-IDP, APP-RP can set a RedirectCookie with UI-RP-Alice (including the associated XSRF-Token). This RedirectCookie is physically bound to the user agent UI-RP-Alice.
- If the redirection is performed using HTTP-302/-303, the XSRF protecting the RedirectCookie must be stored in the state parameter associated with the redirect_uri. 
- If the redirection is performed using HTTP-202, the XSRF parameter can be held in the local/session storage of UI-RP-Alice before forwarding control to UI-IDP-Alice (beware of the origin policy while keeping object in local/session storage).
- Native Applications have a better control on the persistence and protection of those Cookies and XSRF-Tokens.

While redirecting back from APP-IDP (APP-IDP -> UI-IDP-Alice -> UI-RP-Alice -> APP-RP), APP-RP will only accept the redirect code if UI-RP-? provides the original RedirectCookie (and corresponding XSRF parameter). With this approach, intercepting the authorization code will not help, as UI-RP-Bob will not have access to information stored by UI-RP-Alice.

Despite this countermeasure, redirection still gets complicated when UI-IDP is a native application. Automatic back redirection in this case will start the system default web browser that might be different from the web browser used by Alice to start UI-RP. Alice.

### Malicious Relying party 
Bob could install a relying party (APP-RP-Bob) that can impersonate the original APP-RP to request a token on its behalf. Recall that most relying party server applications will be deployed by companies with not enough experience in application security. So stealing static configuration information like the client-secret won't be a major issue for well trained attackers.

In order to prevent the malicious RP (APP-RP-Bob) from using an intercepted authorization code to obtain the token, IDP and RP can be required to implement server side [PKCE RFC7636](https://tools.ietf.org/html/rfc7636). The advantage of PKCE over classical RP authentication information is that the client-secret (and other APP-RP credentials) are static while generated code_verifiers are dynamic and vary with each authorization request. 

## Embedded SCA Approach

### Reviewing the oAuth2 Resource Owner Password Credentials Flow (ROPC)
ROPC is one that allows a relying party to collect user credentials and take them directly to the token endpoint of the identity provider. 

From a technical perspective, this flow seems to be the simplest as it involves no redirection. From a data protection perspective, this flow discloses the password of the user to the RP, raising issues hard to deal with as we know that the user’s password is not supposed to be shared with non-regulated third parties. We also assume that non-supervised RPs are not trustworthy enough to guarantee the secrecy of the end user's password. 

### Highlighting the Open Banking Embedded Approach
The European PSD2 initiative ensures that all Payment Service Providers participating in the Open Banking arena must be licensed, i.e. security audited and supervised, and it mandates Strong Customer Authentication (SCA) involving a minimum of two factors, i.e. credentials. This includes an SCA method called "Embedded Approach", which allows a licensed third party provider (RP) to collect user credentials (user banking password and transaction numbers) and forward them to the user's banking service provider.

Just like the oAuth2 ROPC, the PSD2 embedded SCA approach allows a licensed TPP to see the password of the end user. But despite the oAuth2 ROPC, the PSD2 embedded approach mandates a second factor which is inherently bound to the transaction being authorized (or the consent being given). With the second factor as enhancement and the requirement of implementing that second factor for authentication with the bank’s native online banking interface, disclosing the online banking password of the banking users only to supervised third party banking service providers (TPP) seems to be a less risky alternative than having banks and TPPs implement technical redirection based solutions they can't control.

## Recommendation : Mandate the Embedded SCA Approach

Considering the fact that all open banking transactions implement a second factor (even for the PSU identification), having the online banking password of the PSU disclosed to licensed TPPs exposes less risk than having to mandate TPP-implementation of complex user  agent redirection.

Therefore, this paper recommends the __mandate__ of ASPSP support of the __Embedded SCA Approach__:
- as second factor is simple, well understood and applied to ASPSP's native online banking interfaces as well,
- as __Embedded SCA Approach__ will free banks from having to implement and operate user agent redirection without having gone through the experience of operating redirection based identity providers,
- as __Embedded SCA Approach__ will release TPPs from the obligation of implementing complex and error prone user agent redirection.

Protecting the PSU's permanent online banking password can further be done by replacing the permanent online banking password with a time base one time password (TOTP), thus removing the residual risk of disclosing that permanent PSU's password to TPPs.

