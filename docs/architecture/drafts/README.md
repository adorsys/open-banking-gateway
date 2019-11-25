# Tech setup
    Spring Boot >= 2.2
    Flowable BPMN 6.x
    Postgres RDBMS 12.x
    XS2A-flow for tests
 
# Least annoying API:
    TPP creates user profile (name, surname,...) that provides information necessary to perform requests on his behalf
    TPP provides setting which mode will be used STATIC or DIALOG to perform user requests
    In STATIC mode TPP must fulfill all fields that are not provided by profile but are necessary to perform action (except SCA)
    TPP calls i.e. /pay/IBAN-from/IBAN-to/CURRENCY/amount with parameters from step nr. 3. The only extra stuff to complete the call is SCA

# Initial implementation diagram

<!-- 
Embedding it in RAW, because plantuml is giving 504 when using diagram with embedded plantuml in it
This is what it should be:
![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/drafts/implementation-draft-v0.puml&fmt=svg&vvv=3&sanitize=true)
 -->
![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/drafts/implementation-draft-v0.puml&fmt=svg&vvv=5&sanitize=true)


# API sketch

## PSU wants to pay 100EUR to IBAN 12345

### Generic case

1. Find PSU client bank id `GET /api/v1/banks?name=Deutsche` or `GET /api/v1/banks?bic=12345` -> `{bankId}`
1. View necessary parameters that are required from PSU for `{bankId}` to execute payment: 
`GET /api/v1/payments/{bankId}/{psuId}/{ibanFrom}/{ibanTo}/parameters` - yields 
`{"GEO_LOCATION": "This payment requires client geo-location"}` (Note: this also may yield i.e. SCA method if PSU did not select it in profile)
1. Since request can't automatically proceed, TPP reads required GEO_LOCATION from PSU
1. Now TPP can proceed with payment `PUT /api/v1/payments/{bankId}/{psuId}/{ibanFrom}/{ibanTo} body: {"amount": 100.0, "currency": "EUR", "GEO_LOCATION": {"lat": 12, "lng": 10.0}}` 
(Note: This can have i.e. SCA method to use that overrides profile defaults)
1. OpenBanking handles request sequence using computed process

### Sequence diagram

![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/drafts/payment.puml&fmt=svg&vvv=1&sanitize=true)