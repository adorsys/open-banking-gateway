# Authorize Consent Redirect Approach

## Description

Describes the process of redirecting a PSU to the Online Banking interface of it's ASPSP.

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/5b-psuAuthRedirectConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases

### 010 .. 040 Display RedirectInfoPage
A redirection to the ASPSP OnlineBanking interface start with a information of the PSU about the redirect process. After a confirmation of the redirection process through the PSU, a redirection is initiated by the ConsentAuthorizeApi.

### Managing Redirection

#### 050 Redirecting PSU to the ASPSP
Detailed specification of the redirect process might depend on the specification of the ASPSP interface. Nevertheless, the returned redirect link carries an ConsentAuthSessionCookie that is used to store consent details in the User Agent of the PSU. 
As well the consentAuthState shall be part of any BackRedirectURL (OKUrl, NOKURL) so we can decrypt the ConsentAuthSessionCookie when ASPSP sends back PSU to TPP.

#### 060 Back-Redirecting PSU to the ConsentAuthorisationAPI
The ASPSP url used to redirect the PSU to the ASPSP contains the consentAuthState. The consentAuthState will the be used to retrieve the attached ConsentAuthSessionCookie and retrieve the TppConsentSession.

#### 071 .. 073 Forward call to BankingProtocol
The aspspAuthSuccess method of the BankingProtocol is called with TppConsentSession and aspspAuthCode.
- The aspspAuthCode can be use to retrieve Token from ASPSP Token endpoint in case of an oAuth Approach.
- The consent session contains any other information needed to manage the consent process.

#### 077 Redirect PSU to FinTechAPI
The TppConsentSession is temporarily encrypted and stored. Corresponding redirectCode is used to redirect PSU to the FinTechAPI redirect endpoint. ConsentAuthSessionCookie is deleted with the redirect process.



