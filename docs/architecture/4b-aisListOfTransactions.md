# List Of Transactions
General terms defined in the [dictionary](dictionary.md)

## Definition
Request the list of transactions for a given bank account. 

If there is any reference to an existing account information consent (AisConsent) stored in the database of the TPP, the TPP will use this consent reference to forward the service request to the OpenBanking interface of the ASPSP.

If there is no such reference in the database of the TPP, the TPP will respond the FinTech to redirect the PSU to the ConsentAuthorizationApi of the TPP.

In order to uniquely identify the requesting PSU, the TPP uses a unique reference made out of:
- the fintechId : the unique identifier of this FinTech in the realm of the TPP. This parameter is read from the [FinTechContext](dictionary.md#FinTechContext) transported as jwt-Token in the Authorization header of each FinTech request to the TPP.
- the psu-id@fintech : the unique identifier of the PSU in the realm of the FinTech.  This parameter is transported in the HttpHeader named: Fintech-User-ID

## Diagram
![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/4b-aisListOfTransactions.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Cases
### LoT-010 FinTechUI.displayBankAccount(BankAccount)
After receiving the list of accounts, the FinTechUI can dsiplay a single bank account to the PSU.

### LoT-020 : FinTechUI.selectService(listTransactions)
On of the services available when the FinTechUI present bank account details to the PSU is the "listOfTransactions". If selected by the PSU, the FinTechUI forwards the service call to the FinTechApi. The selection must be accompanied with some mandatory and optional service specifications. For example in the case of listOfTransactions, this the account-id is part of the request path and indicates the target account. The ListTransactionsSpec is used to describe additional optional request parameters.

### <a name="LoT-030"></a>LoT-030 : FinTechApi.listOfTransactions
Call specification: listOfTransactions[SessionCookie,X-XSRF-TOKEN,X-Request-ID]()<p:bank-id, p:account-id, q:dateFrom, q:dateTo,q:entryReferenceFrom, q:bookingStatus, q:deltaList>
The FinTechUI issues a listOfTransactions request to the FinTechAPI with:
- __[SessionCookie and X-XSRF-TOKEN](dictionary.md#SessionCookie):__ The SessionCookie used to maintain association between FinTechUI and FinTechApi. It holds a session identifier. A corresponding XSRF-TOKEN is sent back and forth though the header and used to authenticate the SessionCookie.
- __The bank-id:__ passed as a query parameter and referencing the given [BankProfile](dictionary.md#BankProfile) that contains meta information associated with the selected Bank.
- __The account-id:__ is sent as a path parameter and references the target bank account.
- __dateFrom:__ Starting date (inclusive the date dateFrom) of the transaction list, mandated if no delta access is required. For booked transactions, the relevant date is the booking date. For pending transactions, the relevant date is the entry date, which may not be transparent neither in this API nor other channels of the ASPSP.
- __dateTo:__ End date (inclusive the data dateTo) of the transaction list, default is "now" if not given. Might be ignored if a delta function is used. For booked transactions, the relevant date is the booking date. For pending transactions, the relevant date is the entry date, which may not be transparent neither in this API nor other channels of the ASPSP. 
- __deltaList:__ This data attribute indicates that the FinTech is in favour to get all transactions after the last report access for this PSU on the addressed account.  
- __entryReferenceFrom:__ This data attribute indicates that the FinTech is in favour to get all transactions after the transaction with identification entryReferenceFrom alternatively to the above defined period. This is an implementation of a delta access. If this data element is contained, the entries "dateFrom" and "dateTo" might be ignored by the ASPSP.
- __bookingStatus:__ To support the "pending" and "both" feature is optional for the ASPSP, Error code if not supported in the online banking frontend Default is "booked".
- __X-Request-ID:__ unique identifier that identifies this request throughout the entire processing chain. Shall be contained in HTTP Response as well.

### LoT-031 : FinTechApi.checkAuthorization
Call specification: : checkAuthorization(SessionCookie,\nX-XSRF-TOKEN):psu-id@fintech
Before proceeding with the request, the FinTechApi must validate the request for it authenticity and extract a unique identifier of the PSU in the world of the FinTech (psu-id@fintech). This validation also include the matching of the used cookie against the provided XSRF-Token.

### <a name="LoT-032"></a>LoT-032 : FinTechApi.userAgentContext
Parses the HTTP request and extract information associated with the user agent (see [UserAgentContext](dictionary.md#UserAgentContext)).
The __[UserAgentContext](dictionary.md#UserAgentContext)__ describes details associated with the user agent of the PSU. Generally not visible in the API as they are automatically provided by the user agent. The purpose is to transfer context specific information on both current Request and PsuUserAgent. Those information might later be required by the ASPSP like. Below is a non exhaustive list of UserAgent specific context information:
  * IP-Address,
  * IP-Port,
  * Accept,
  * Accept-Charset,
  * Accept-Encoding,
  * Accept-Language,
  * Device-ID,
  * User-Agent,
  * PSU-Geo-Location,
  * Http-Method.

### <a name="LoT-033"></a>LoT-033 : FinTechApi.buildUrlTemplates
Builds URL templates used to redirect control to the FinTechUI application. Generate a state parameter that will be used to protect the redirect process and add it as a query parameter to the built Fintech-Redirect-URL templates that are added to the request. The templates have the form __Fintech-Redirect-URL-[OK|NOK]<p:$AUTH-ID,q:state>__. Both the OK-URL and the NOK-URL are used to redirect control to the FinTechUI application from the TPP consent authorization interface (ConsentAuthorizationApi), as we set a RedirectCookie in the FinTechUI before redirecting the PSU to the ConsentAuthorizationApi. This RedirectCookie must be transported back to the FinTechApi with the redirect call back to the FinTech. 
  - In order to distinguish redirect cookie associated with different authorization flows, we scope the Fintech-Redirect-URL with a dynamic path parameter "$AUTH-ID". This must be set as part of the cookie path of the RedirectCookie returned to the FinTechUI.
  - In order to protect the Fintech-Redirect-URL against XSRF, we use the state parameter. This state parameter is defined at initialization of the request, and added as a query parameter to the Fintech-Redirect-URL.

### LoT-034 : FinTechApi.loadServiceSession
Uses the given psu-id and service type to load a corresponding service session if the FinTech judges the request of the PSU is the repetition of an existing service request.


### LoT-040 : TppBankingApi.listOfTransactions
Forwards the PSU request to TPP with following associated context informations:

- __Authorization:[FinTechContext](dictionary.md#FinTechContext): __ contains static identification information associated with the FinTech.
- __Fintech-User-ID: psu-id@fintech:__ the unique identifier of the PSU in the realm of the FinTech
- __Service-Session-ID:__ a unique identifier of the service request. This is returned by the TPP as a response to the first HTTP-Request associated with this service request.
- __Fintech-Redirect-URL-[OK|NOK]<p:auth-id,q:state>:__ these are URL used to redirect control to the FinTechUI application. See [LoT-033](4b-aisListOfTransactions.md#LoT-033).
- __UserAgentContext:__  See [LoT-032](4b-aisListOfTransactions.md#LoT-032).
- __Bank-ID: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __account-id: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __dateFrom: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __dateTo: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __entryReferenceFrom: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __bookingStatus: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __deltaList: __ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).
- __X-Request-ID:__ See [LoT-030](4b-aisListOfTransactions.md#LoT-030).

### LoT-041 TppBankingApi.checkAuthorization
verifies the authenticity of the Authorization header "FinTechContext". Returns the extracted fintechId.

### LoT-042 TppBankingApi.serviceSpec
Put service parameter in a serviceSpec map for further processing.

### LoT-043 TppBankingApi.serviceContext
Put all objects associated with the call into a generic ServiceContext object.

### LoT-050 .. LoT-052 BankingProtocolFacade.service
See [ListOfAccounts]((4a-aisListOfAccounts.md#LoA-050).)

### LoT-060 .. LoT-067 : BankingProtocol.service
See [ListOfAccounts]((4a-aisListOfAccounts.md#LoA-060).)

### LoA-070 .. -080 : No Suitable Consent Present. Redirect
See [ListOfAccounts]((4a-aisListOfAccounts.md#LoA-070).)

### <a name="LoA-090"></a>LoA-090 Suitable Consent Present
If there is a suitable consent reference in the database of the TPP, this will be loaded and used to forward request to the ASPSP.

### <a name="LoA-091"></a>LoA-091 : Forward Service Request to ASPSP
Service request is forwarded to the [AspspBankingApi](dictionary.md#AspspBankingApi) together with a reference to an AisConsent. The Associated [TppContext](dictionary.md#TppContext) contains TPP identifying information.

### LoA-092 .. LoA-95 : Returned Service Response
The returned ListOfTransactionsResponse is wrapped into a BankingProtocolResponse<ListOfTransactions> that will travel through the call chain back to the FinTechApi.

### LoA-096 FinTechApi.storeServiceSessionId
The FinTechApi will first store the service session for future reference.

### LoA-096 FinTechApi:200_Accounts
The FinTechApi returns the payload to the FinTechUI together with a new SessionCookie. 
