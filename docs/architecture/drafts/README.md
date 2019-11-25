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
![Implementation draft diagram](http://www.plantuml.com/plantuml/png/ZLPHRzis47xdhpZqPHiwUurTTuk11BKJ9t2n94AS00FcFj3YMIOiaHf9BScA_FVnA36I1HTR-s3dxhkFn-_8Z_vi7JUkAd9czrALtF02b1PeiOJHXqxGSBMdu6J2ViXnwy1fC3BREH3IOEgaLepnr6a3oUgHCOfnbUK82NyfKBbx_Aj2w-1F1W2pGbVAJU7amuV5upr5XCz9nOd7XqVZZnEAplsgKxXveK_sbR5K5wLMdWtUtPMeOEFZKcMGSOTF_0LcoV8TS0jtynby8uAsmAArrEGhOu8xlk4M3qdMAhB4VAIwd0SoqWLSMJvQD8dHMvbZlHjOETzu2fLFG5bdGdmm68JFMrxW5BxedQhXbL4XSgbht5pcEMs32s5ycrEuhGmM6pJPpsjrLHctyyJM6MujmkJKrtoD_L3pXysGLiUw8770S8UGoq86yNyH_ANkw6861RgT5dxnXLzP21JzPIwqsigiCc58tSNIWyIFptR41rpmqg7vgTz1kqUh2znfwuP-HirIdV4safhcr4YrrJIE65Zwlnc3_ZZEpiuF4D3LvtYsjwq-vlNWqFsn6iq6itfhePrccrtCW-7AIlhQy8Jp5D0kfYvz99ofzfgyMoQjDYV3y3yKBPDU4mz94evQ48Kc_RSKJJlux9SWGQZt_WBNg7_NQNF59f-6amdatC5uD1oF8LTPOBXUtD5ohEJfdcU-QfxSt0AgJ2eaGFCrS7Tw-gAbWhtIJpaANyKtkdAXIQff4uT5P0dIe82jn5nOs6e3AS_pEEYGVC2Yz6tsIYzscEv1Ri73Wxvh9QtN0YsbYQWHFt1nkx_IPa7Ya327L3VQO93I546RjOfBztcWyD1bYxn552WEsKvEXd2FjigTL5aiNYjBNrIAK735Cq-zUG5IjjbgWsyyye1nrLGs5mv2sHjDMPjha_f6Kud_akKgWS4vp5Q9__B-FKoZ5ODKqicmXTXXfywWgumwZhM-AOj75l6Ux_cl4CM6-y4CW5Tkfuty1-j3pyCBqIdCjIw3xSmQ8Ey1W1hlaXCutVbveygm2wHykz4fN4HG8sm7YqhyFuMiuwwo_wLEnFMLUNqbGplc7Kj_c94gmtR3zCakATp5agrICXWKOxJ1i_3QdftttrCo2uerRoczoylvxBP-GzUgib9byGNjbPBNDS-YfndvWxjOFOGRo9emGUADdy9u21U-nyf5PSEhsMX42zCMMSSzGtdh2bFuI9U32x3jWxbMN0dOT1-jGvliSqpQBjw0QzNrMdPeWts2JqDOF9T-rJQwLa_IxI3KTRs0jNN4qlCHp_LhzxQtDjSph-zL_zPk9LPuEKnMqPJicle8cW8lPJsNFWVn7dKbptww6SArXWnTWDJI70SJ3VrceXy5lcfczq2lJFrZBlHnH9llK_KDZQn1vZa4aTjU-Z2gRVrk6hKHO6ECfwas8XmxfzyEZTEfFYBDyKPE-H0IDDHsa3Gm1P5doPB5rQawFkwaoiXFoeC3SD34MSmJDjvY0e-xO6ADoVOIK-i4zHTYgJo7uZde4asjB6KZDjhVoQJjsriX-unAL4N-Bm00&fmt=svg&vvv=11&sanitize=true)


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

#### TPP acquires mandatory parameters before request 

![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/drafts/payment-static-param.puml&fmt=svg&vvv=2&sanitize=true)

#### TPP acquires mandatory parameters during request 

![Implementation draft diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/drafts/payment-dynamic-param.puml&fmt=svg&vvv=1&sanitize=true)