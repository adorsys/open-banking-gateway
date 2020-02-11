# TPP PSU Consent Session API

Provide interaction between PSU and TPP during a consent session.

The purpose of a TppPsuConsentSession is to manage a single consent session between the PsuUserAgent and the TppConsentSessionApi. The TppPsuConsentSession starts with the redirect of the PsuUserAgent from the TppBankingApi to the TppConsentSessionApi.

## Running

For now just simple starter in test package.

Run the class: opba-consent-rest-api/src/test/java/de/adorsys/opba/consent/rest/impl/SampleApplication.java to start a spring-boot server process.

swagger-ui: http://localhost:8080/swagger-ui.html

## Description

Following environments are involved in the consent initiation process.

| Environments | Description  |
|---|---|
| PsuUserAgent | The web browser and/or mobile device used by the PSU to access banking functionality.  |
| FinTechServer | Data center environment of the FinTech |
| TppServer | Data center environment of the TPP |
| AspspServer | Data center environment of the ASPSP |
|---|---|
| __Applications__ | __Description__  |
| FinTechUI | UI Application running on the PsuUserAgent and used by the PSU to access the FinTechApi |
| FinTechApi | Financial web service provided by the FinTech. |
| TppBankingApi | Tpp backend providing access to ASPSP banking functionality|
| AspspBankingApi | Api banking provided by ASPSP  |
|---|---|
| __Header Groups__ | __Description__  |
| UserAgentContext | All information associated with user agent of the PsuUserAgent. Like PSU-IP-Address, PSU-IP-Port, PSU-Accept, PSU-Accept-Charset, PSU-Accept-Encoding, PSU-Accept-Language, PSU-Device-ID, PSU-User-Agent, PSU-Geo-Location, PSU-Http-Method |
| PsuFintechSession | This is a cookie used to maintain the session between the FinTechUI and the FinTechApi |
| FinTechContext | Information used to identify the FinTech application in the TPP environment. Like a FinTech client certificate. |
| TppContext | Information used to identify the Tpp application in the ASPSP environment. Like a TPP QWAC certificate. |
|---|---|
| __Payload__ | __Description__  |
| ConsentData | Specification of the requested consent. BankAccount, frequencyPerDay, validUntil, ..., TppConsentSessionRedirectUrl |
| Tpp2AspspConsentSession | Information associated with the consent initialized by the ASPSP. Containing ConsentId, AspspConsentSessionRedirectUrl |
| Fintech2TppConsentSession | Information associated with the consent as returned by the Tpp to the FinTech. Containing authCode, TppConsentSessionApiUrl |


