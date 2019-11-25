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
![Implementation draft diagram](http://www.plantuml.com/plantuml/png/ZLPXZnCt4FtkNp7YIrlHf5oEq1Bf4BaZXuBAiIA7LAdXWxEU95PsxQtj1Qxe_dizizxRpHBQmeVankyzZv_jvxpqGRfGbuNm1sqgwMG9nYhqMC7aIQ_ef3bGSJeLFnIu3H0iEBtR1r3QOHwqDKB8F5W7sUg345IJPbSWPFAkH1FUuryr-W1_2W2ObxOsOGPdJvuiFhodYefZsaZIYUN9-TCfLI_Zh3DuUqUVugCGkIqhQw8QF7fNeO5Dh6kpWvqC-5dUmJnRFWBfuTtb7BwIG4Vmw3qrUI-4aa5kfCTZaRLfLThnLkhL9OcnBn2gweD7bpcxrGKsgu5LY8tdKCS1g9eHhez68_wyaINEu8tTcmPUEyELLxR1NUgYe6L8fLniSmOtjSDoWsxtyzfSroxiex0FJdgFC7qUEHzJFzpySJFarQako1nmCY0KkjHi_YzAtZKTNSsXnB2tAaw-Y3ChXMeupPKrMxsh7MzIVxByQE37BtuYHrB9Ag3xQTX1jqPlIznR7yPnZz9KlUrDbhfqQhJPMjwEL5Z6luM0uNPSNBmuGW0t_c-ZmsKDCVT7X-wFrMG-cZTButRIClkOMoUDr_GrwV1vOdHeXrx52f-fxfeyMcQTDsTZ_i-aPJPeuZRB-AYnARJJdxHAxU2NE0KPGhqF9tYDzdURfoisVJQUJg6G0Sudu_DpACoE5Lultj5qefBvGUuYwp9xUmDeTjeW0T9NrkxrzCPg0mTZFnUe8ajkR1suIU9q0ySaYWJjKC5MOw4yRAs3N1O5SrmQlCMoYcqEg5Txp0-Wjn3XxE_QQ1-zG4_399JCPotf3zVMBSWSCkPOwgrro5QwahrPctRgRtJA25rso1j4XUfOxUni3E_HrqMW2vt8Q-FfYyaHIcdaBaflxe2SJKjD-9IHHughn4mN3fZs85CruPg6ljAkf7yKmp1w0TbobS7Znp3ZWZOwQ1cm9S8dBI4Ug8x712BELza3Cu6PpKjlLzRZEGorCwUnFYx6uxVWQqIraN4dHo3h63zE_utDBP4vKNkCmjgASsgUW7800EgOM-ULpXYSxsLHeE6JDxGcuMTmrOBIJlImQDJ_iyO76Mh_txOatC2I-tjAm5DXiuod7ud9o-NZGlbAvJwMSfW7AT64e0LUyFDyyMBu05EwK2qzlX0iB2_dDysZkpQrfuERdjm1bS8nlQDHPX9F-c9rorTMf393KaJCu7mCLx77Ec1tnjScUKGJqn95BswPtiN839xIRP8AVFV2heqq2ZRzL-uuLuSQqwwB1-3QzCDP7EVcKE3P61PVgZXhLrsRpphiWNdzy10TZrG6mHErVkt7GvUBmxJx3lktdg694UrmkvfsoQ-f3_OKPAMRVHbgaEx968lgppTZU6rXXu4XZJMdmIH3FxBeLqHapVs1KWIQNt_SnmbllYyrJC2eUwsB0jZahfSXZBXTGCvQRnJujYRpt3h5noxOXqCJR6vFMtEYaUTo31awQfijPIKocNxEY7PsPgTV0-JA9EvK13DmbEeYVJAslSK4Fk-3IRKTx2unjKxGU26M9ceOMO2jq3NEycZ2jl5tDktwZb4eNg9HTLdy0m00&fmt=svg&vvv=10&sanitize=true)


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