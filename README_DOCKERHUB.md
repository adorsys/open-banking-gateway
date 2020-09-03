# Open Banking Gateway Project

## containers

### Postgres

Postgres is used as database by all servers and should be started first in order to register all servers.

### Open-Banking-Gateway Server

This server provides tools for transparent access to open banking apis and focuses mainly on the connectivity to banks's apis that are compliant with the European PSD2 directive.
To be compliant with the PSD2 Directive the Open-Banking-Gateway Server provides a Consent-management-System that enables for example an Online-Banking Application to access bank services,
a TPP-System that exposes APIs to Fintech application to access bank APIs since this last one is not regulated and a MultiBanking-System which facilitates the integration of
several Banks APIs that can be used to look for a bank we want to interact with. Two protocols are implemented in this project which are XS2A-Protocol and the HBCI-Protocol.  
In other words the OpenBankingGateway integrates a set of applications together around the XS2A, each playing a precise role as explained earlier, to show how a user from an OnlineBanking
application or a FinTech application can access his data at a bank.

#### - Adorsys-Sandbox

The Adorsys-Sandbox application uses the XS2A interface as described by the Berlin Group as Bank-Services, a ledger responsible for recording accounts and transactions
like in a real bank, an Online-Banking-Server for authorization and a Developer Portal for testing purpose. This Sandbox just expands the XS2A-Services with on online-banking-application and
a ledger as database to simulate a real bank that can be tested on the Developer Portal.
The OpenBankingGateway uses the Adorsys-Sanbox as bank data-center for all accounts, payments and transactions computing.

### HBCI-Server

The server implementing the HBCI interface which defines transmission protocol, messages formats and security procedures for home banking
and is used to in the project when the HBCI-protocol is chosen. The HBCI-Server manages the different rules described by the HTC-Protocol when the Bank choses to
support this protocol.

### Fintech-Server

The Fintech-Server is an example of FinTech-Application that can get user data provided by the TPP-Server since the PSD2 Directive has mandated the regulation of TPPs (Third Party Providers)
that enables them to expose APIs to FinTechs. This server simply represents the data provider for the FinTech UI which is responsible to get user data from the OpenBankingGateway and also
initiate a payment on behalf of the user as if he was on an online banking application.

### Fintech-UI

The FinTech-UI is an Angular web application that represents the client that interacts with the FinTech-Server and can show user's accounts and transactions.
When a user logs in the FinTech-UI he can authorizes the FinTech-UI to access his accounts and transactions or to initiate a payment from the TPP APIs by getting a digital consent to
from the consent-management-system through the FinTech-Server. So this FinTech-UI could be any kind of frontend application that can read data from the OpenBankingGateway through a
FinTech Server.

### Consent-UI

The Consent-UI is an Angular web application uses by a FinTech-UI to hold a digital consent that enables the FinTech-UI to get user's accounts and transactions
from the FinTech. It represents the UI of the consent-management-system which manages accesses to TPP APIs. Because the FinTech UI need to read and proceed user data it has to
get the necessary authorization from the consent-management-system and for do so this Consent UI is an example of a frontend application to help him get the necessary wrights.
