
# Tech setup
    Spring Boot >= 2.2
    Flowable BPMN 6.x
    Drools rule engine (embedded with Flowable)
    Postgres RDBMS 12.x
    XS2A-flow for tests
 
# Least annoying API:
    TPP creates user profile (name, surname,...) that provides information necessary to perform requests on his behalf
    TPP provides setting which mode will be used STATIC or DIALOG to perform user requests
    In STATIC mode TPP must fulfill all fields that are not provided by profile but are necessary to perform action (except SCA)
    TPP calls i.e. /pay/IBAN-from/IBAN-to/CURRENCY/amount with parameters from step nr. 3. The only extra stuff to complete the call is SCA

# Initial implementation diagram

## E2E flows

1. [Transaction list](#full-swimlane-e2e-for-getting-transaction-list) flow.
1. [Performing payment](#full-swimlane-e2e-for-performing-payment) flow.

## Generic request handling (Check consent is valid pre-filter)

Initially (for MVP0), questions `Needs consent?` and `Consent valid?` are answered using database-table that 
contains bank profile, in future it can be switched to rule engine or entire BPMN job. 

### TPP can initiate consent with PSU under the hood if required. 

This basically means that if request is missing consent, but has flag to allow automatic consent creation TPP will 
call [obtaining consent](#obtaining-ais-consent-swimlane) flow.

![Implementation draft diagram - generic request](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-v0-generic.puml&fmt=svg&vvv=2&sanitize=true)

## Obtaining AIS Consent swimlane

![Implementation draft diagram - consent](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-v0-consent.puml&fmt=svg&vvv=1&sanitize=true)

## Getting transaction list using AIS Consent swimlane

![Implementation draft diagram - Tx list](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-v0-tx-list.puml&fmt=svg&vvv=1&sanitize=true)

## Full swimlane e2e for getting transaction list

<details><summary>Get transaction list by FinTech</summary>

**Notes:**
1. Initially PSU enters FinTech screen 'Transaction list'
1. Since consent is missing, when being asked for 'Transaction list' TPP will create implicit consent with PSU 
(since PSU is in session with FinTech)
1. After consent was established FinTech can store it and get 'Transaction list' without PSU intervention
1. If consent has expired FinTech should inform user and perform step 1 again 

![Implementation draft diagram - Tx list full](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-v0-full-flow-tx-list.puml&fmt=svg&vvv=1&sanitize=true)

</details>

## Full swimlane e2e for performing payment

<details><summary>Perform payment by FinTech</summary>

![Implementation draft diagram - Perform payment](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-v0-full-flow-payment.puml&fmt=svg&vvv=2&sanitize=true)

</details>

## API

OpenBankingGateway Api is defined for boundary (of course TPP itself can use it):
FinTech with OpenBankingGw API <--> TPP with OpenBankingGw Impl (See diagrams above)

Consent API:
1. `PUT /consents/{bankId} body: {accounts: [<accountIds>], allAccounts: true}` to create consent

Account information API:
1. `GET /transactions` to read transactions **with option to ask for consent automatically**
1. `GET /accounts` to read account details **with option to ask for consent automatically**

Payment API:
1. `PUT /payments` to initiate payment
...


## In short

![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-v0-bird-view.puml&fmt=svg&vvv=1&sanitize=true)

## With details

![Detailed Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/implementation-draft-v0.puml&fmt=svg&vvv=17&sanitize=true)


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
1. Open Banking Gateway handles request sequence using computed process

### Sequence diagram

#### TPP acquires mandatory parameters before request 

![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/payment-static-param.puml&fmt=svg&vvv=2&sanitize=true)

#### TPP acquires mandatory parameters during request 

![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/drafts/payment-dynamic-param.puml&fmt=svg&vvv=1&sanitize=true)
