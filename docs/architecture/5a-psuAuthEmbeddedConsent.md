# Authorize Consent Embedded Approach

## Description

Implements the process of collecting consent authorization credentials in an interface provided by the TPP as described EBA-RTS embedded approach. The authorization is designed as a recursive list of ChallengeResponse sessions.

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/5a-psuAuthEmbeddedConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### AuthEmbedded-010 : Create ConsentAuthSessionCookie

If the TppConsentSession has an authChallenge, the interaction starts with the initialization of a [ConsentAuthSessionCookie](dictionary.md#ConsentAuthSessionCookie). The ConsentAuthSessionCookie is encrypted with a key stored in the [consentSessionState](dictionary.md#consentSessionState).

### AuthEmbedded-020 : Redirect to EmbeddedAuthInitScreen

After preparation of the ConsentAuthSessionCookie, the UserAgent is redirected to the EmbeddedAuthInitScreen of the ConsentAuthorisationUI.

### AuthEmbedded-030&-040 : Load AuthChallenges
The generic endpoint at ConsentAuthorisationApi.embeddedAuth allows the ConsentAuthorisationUI to load AuthChallenges if any. The call returns the AuthorizeResponse that contains all information necessary to display returned challenges to the PSU. An ScaUIMetadaData object contain UI customization parameter.

### AuthEmbedded-050&-060 : Display Auth Screen and Collect PSU Auth Data
Using information contained in the AuthorizeResponse object, the ConsentAuthorisationUI will display the suitable AuthScreen to the PSU and use it to collect PsuAuthData.

### AuthEmbedded-070..-087 : Send PsuAuthData to ConsentAuthorisationApi
The generic endpoint at ConsentAuthorisationApi.embeddedAuth will finally be called again to send authentication data entered by the PSU to the BankingProtocol.

### AuthEmbedded-090..-094 : Redirect to FinTechApi
As the TppConsentSession presents no more AuthChallenge, a RedirectSession is prepared and the PSU is redirected back to the FinTechApi.
    
