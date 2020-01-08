# Authorize Consent Redirect Approach

## Description

Describes the process of redirecting a PSU to the Online Banking interface of it's ASPSP.

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/5b-psuAuthRedirectConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### AuthRedirect-010 & -020 : Display RedirectInfoPage
ConsentAuthorisationUI.infoPanel page uses information provided by the AuthorizeResponse to display a redirect to ASPSP info page to the PSU. 

### AuthRedirect-030 & -040 : Grant Redirect
The PSU interface might decide to either allow the PSU to explicitly confirm or deny the redirection to the ASPSP, or automatically proceed with this without the consent of the PSU. In both case, the ConsentAuthorisationUI has to invoke the FinTechApi.toAspspGrant that will in turn invoke the ConsentAuthorisationApi.toAspspGrant endpoint to generate the redirect response. 

### Managing Redirection

#### AuthRedirect-050 : Redirecting PSU to the ASPSP
Detailed specification of the redirect process might depend on the specification of the ASPSP interface. Nevertheless, the returned redirect link carries an ConsentAuthSessionCookie that is used to store consent details in the User Agent of the PSU. 
As well, the consentAuthState shall be part of any BackRedirectURL (OKUrl, nokUrl) so ConsentAuthorisationApi can read the ConsentAuthSessionCookie when ASPSP sends back PSU to the ConsentAuthorisationApi.

#### AuthRedirect-060 : Back-Redirecting PSU to the ConsentAuthorisationAPI
The endpoint ConsentAuthorisationAPI.fromAspspOk consumes a redirect call from the ASPSP Online Banking. The corresponding URL
contains a consentAuthState. The consentAuthState will the be used to retrieve the attached ConsentAuthSessionCookie whose content will in turn be used to read the TppConsentSession.

#### AuthRedirect-071 .. AuthRedirect-073 : Forward call to BankingProtocol
The fromAspspOk method of the BankingProtocol is called with TppConsentSession and aspspAuthCode.
- The aspspAuthCode can be use to retrieve Token from ASPSP Token endpoint in case of an oAuth Approach.
- The consent session contains any other information needed to manage the consent process.

#### AuthRedirect-077 Redirect PSU to FinTechAPI
The TppConsentSession is temporarily encrypted and stored in the form of a RedirectSession. Corresponding redirectCode is used to redirect PSU to the FinTechAPI redirect endpoint. ConsentAuthSessionCookie is deleted with start of this back redirect process.



