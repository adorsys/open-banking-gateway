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
![Implementation draft diagram](http://www.plantuml.com/plantuml/png/ZLRVRzis47xdNt78opPqyHAdxbQZ3MgdJk5YJOKw1GRKVQ35iqoO8ZMIQfCL-T_7eyH8LbtinqFSk---EtuaF-gLyzpwkbJCxQMkkEKbQ2FGOGNZirxGShsdu6J2VbAuzU0DM5diF0XfCVVIQCPuxes5RFMHCOfnNIY4ZD-NgFq7_Bj6v-4J0u1PQMhjft1-ThRu-84Y8kIaviGJmkEBfnEApaFNARoxfx_iCsEvAIkZ0nkSlAzGmoR4fIwWu1w_ydkOPSiJu0xUpsVmZGYw0eVEqP0FZ0dk-OOxF2HPwyIIyedgzPp8eYxWg-gZGvjPivKAczN0oeV1SwX30gec4-ClNvx6lpUyn2cyDJlTm6khO-IrQN1pgHGjWmjXmvXJkAajbXkqnQzhVLrRlqCBpblk7CBaUQZvdEQXuG-78QsEJK7YWEKUGSbIHl5_4_o-cUXg1YNwdH6X-I9q5WB5iCsLqLjPr3PkKhzPVf3u-SwD-IaNlF9eVnbEqAtHcH9tnlbHsAEsLMzxMqbjUsgatXhQZXHOXdyp1eFj28qk3n3Gr-TuTh2i8URXuD3zjHhFJcVDqk8uxJBxc5lBjPFqCmrFvocYVKgz3f5ufhfhShBCEctEH_7_MBJC1aFSPbayQb4KskalKhJZu5reGOBGxCC6Rz3yQVBsYasUZIOJKDp3nNXqSG5A5v7XpU8zjMCLp_Uy25NpxDqDe2waHWAqFoDtRwQtHchOQ_DLeGXLV6Dg7uUacYvnM4IM82qAs4fKmi7MMCYvKccZO_8MoogCEIYzsc6-1xc508_whhLqGGjqb0v4IVncbzp-sjW5YKF275AzCnQZbBQCsgnrQlqTJncWomvvWoXG7BATdu_W0xfQUQcBLBpMZdxe7A7aEiWgO7CFf6opr8HlFVA0STLKfjiDiUoHfchCjKrzerr9__7Q6rsJYy39YytbYzmeOs6ZQhpyT7Rs-QI3acF3wILaorK6Jvx0D0QabbvoZwa7V94Smjdhwc815SvMsMDb2uxLKQKaGg_Eeg-jFevrOTqElmTV8ueD3vj-IYlR6Il_mUP2yPnAUnNAc2fQsgm5yW40uiHnT5hfD8Bp7LSATJoaGsbQ_1Ik4gZTj1uMjVX_qZZFVUt-KvQ46qZoy41cUSoNbcMba2hZSkF98YkcS1zBbXsJP7wCqGB9z0RFD7aGnTed6ho1vNnsqppDQrqxgOlqC0zA0rjwRGFDEDo7nUesNcoMmY_f4oB0MZEPmiK8hiA8jK_2nWUvbOYQqmfPpvXZbyvmfl2KxXqNuBgtUAsv5h3fluU73Zpac7HJF0BNkc_ZxD1XXmJFHh2ugqBNBhhMNwNVGQphsmphweXbO561w_U-aNGEElJ57rJ_qIqbLGGvR57JJharpH4r1Lx9Pc-67CHxrF02-_FD2DuOAD17I2FDSJ3HqESOVM-4gfdRaub0yvqOvpYYpO-fXbuPUA-bKX15xcOPmgYsiz9fqaQ0Ip6UvyQAUEoyUJmqtkJcY3J71tdEHv2XfR6ZfCGKHQOF7vQwKtNwRY1Lnc6d0ZW2qsSJItd2fajCu8i-c5XJCY9fQaA5kx3KeLnp1QP2suXAMOAHy8IQTDC6_sElK8kwLFy2&fmt=svg&vvv=3&sanitize=true)


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