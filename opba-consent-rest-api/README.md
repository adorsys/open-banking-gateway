# TPP PSU Consent Session API

Provide interaction between PSU and TPP during a consent session.

The purpose of a TppPsuConsentSession is to manage a single consent session between the TPP and the PSU. Note that there is no direct one to one relationship between the TppPsuConssentSession and an AspspConsent

![How privatespace diagram](http://www.plantuml.com/plantuml/proxy?src=https://https://raw.githubusercontent.com/adorsys/open-banking-gateway/master/docs/architecture/diagrams/3-initiateConsent.puml&fmt=svg&vvv=1&sanitize=true)  

## Supported Standards

In the embedded approach, the TPP collects PSU credentials and forward them to the TPP. This is used to cover:
- The Berlin Group XS2A Embedded Approach
- The legacy HBCI interface
- Most traditional screen scraping based country implementations of access to account.

## Object Model

This API brings an object model that can be used to display:
- An Account information consent
- A confirmation of fund consent
- A Payment

## Session Tracking

The API uses cookies for session tracking. As we assume the calling interface is the user agent of the PSU. Cookies provide for maximum protection here as we can use them to:
- Store session information while exchanging between user agent and TPP consent backend. This way we wont have to persist any PSU related information in the TPP consent backend.
- State information use to validate legitimacy of the cookie sent will be found in the request URL. 

## 

