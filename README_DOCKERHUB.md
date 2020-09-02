# Open Banking Gateway Project

## containers

### Postgres

Postgres is used as database by all servers and should be started first in order to register all servers.

### Open-Banking-Gateway Server

This server provides tools for transparent access to open banking apis and focuses mainly on the connectivity to banks's apis that are compliant with the European PSD2 directive.
To be compliant with the PSD2 Directive the Open-Banking-Gateway Server provides a Consent-management-System that enables for example an Online-Banking Application to access bank services,
a TPP-System that exposes APIs to Fintech application to access bank APIs since this last one is not regulated and a MultiBanking-System which facilitates the integration of
several Banks APIs that can be used to look for a bank we want to interact with. Two protocols are implemented in this project which are XS2A-Protocol and the
HBCI-Protocol.

####- Adorsys-Sandbox
The Adorsys-Sandbox application uses the XS2A interface as described by the Berlin Group as Bank-Services, a ledger responsible for recording accounts and transactions
like in a real bank, an Online-Banking-Server for authorization and a Developer Portal for testing purpose. This Sandbox just expands the XS2A-Services with on online-banking-application and
a ledger as database to simulate a real bank that can be tested on the Developer Portal.

####- HBCI-Sandbox
The HBCI-Sandbox is the application that uses the HBCI standard interface in production and development environment.

### HBCI-Server

The server implementing the HBCI interface which defines transmission protocol, messages formats and security procedures for home banking
and is used to in the project when the HBCI-protocol is chosen.

### Fintech-Server

The Fintech-Server plays a fundamental role in this project because the PSD2 directive has mandated the regulation of TPPs (Third Party Providers)
that enables them to expose APIs to FinTechs. So this FinTech-Server represents an example of FinTech Application that uses APIs exposed by a regulated TPP to access a payment service exposed by banks.

### Fintech-UI

The FinTech-UI is an Angular web application that represents the client that interacts with the FinTech-Server and can show user's accounts and transactions.
When a user logs in the FinTech-UI he can authorizes the FinTech-UI to access his accounts and transactions or to initiate a payment from the TPP APIs by getting a digital consent to
from the consent-management-system.

### Consent-UI

The Consent-UI is an Angular web application uses by a FinTech-UI to hold a digital consent that enables the FinTech-UI to get user's accounts and transactions
from the Fintech. It represents the UI of the consent-management-system which manages accesses to TPP APIs.
