# Authorize Consent Embedded

## Description

Implements the process of collecting consent authorization credentials in an interface provided by the TPP as described EBA-RTS embedded approach. The authorization is designed as a recursive list of ChallengeResponse sessions.

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/5a-psuAuthEmbeddedConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### 1.0 Create ConsentAuthSessionCookie

If the TppConsentSession has an authChallenge, the interaction starts with the initialization of a [ConsentAuthSessionCookie](dictionary.md#ConsentAuthSessionCookie). The ConsentAuthSessionCookie is encrypted with a key stored in the [consentSessionState](dictionary.md#consentSessionState).

### 2.0 Redirect to EmbeddedAuthInitScreen

After preparation of the ConsentAuthSessionCookie, the UserAgent is redirected to the EmbeddedAuthInitScreen of the ConsentAuthorisationUI.

### 30 .. 40 Load AuthChallenge
The authChallenge returns the ConsentAuthorizeResponse that contains all information necessary to display the challenge to the PSU. An ScaUIMetadaData object contain UI customization parameter.

### 50 .. 60 Display Auth Screen and Collect PSU Auth Data
This STep will display the Auth Screen and collect PSU auth data.

### 70 .. 85 Send PSU Auth Data to ConsentAuthorisationApi
The psuAuth endpoint of the ConsentAuthorisationApi will finally be called to process authentication data entered by the PSU.

### 90 .. 94 Redirect to FinTechApi
As the TppConsentSession present no more AuthChallenge, a redirect session is prepared and the PSU is redirected back to the FinTechApi.
    
