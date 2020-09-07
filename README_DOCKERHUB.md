# Open Banking Gateway Project

## containers

### Postgres

Postgres is used as database by all servers and should be started first in order to register all servers.

### Open-Banking-Gateway Server

The OpenBankingGateway is a set of tools for transparent access to open banking APIs and focuses mainly on the connectivity to banks's APIs that are compliant with the European PSD2 directive.
To be compliant the OpenBankingGateway integrates servers like the Adorsys-Sandbox as computing server implementing the XS2A-protocol and the HBCI-Server 
implementing HBCI-protocol, supports multiple banks and provides APIs and data to financial companies (FinTech) where users can simultaneously 
access their accounts and transactions from different banks and initiates payment. 

#### - Adorsys-Sandbox

The Adorsys-Sandbox application built around the XS2A interface is used in the OpenBankingGateway as computing center for all accounts, transactions and consents that come to the 
OpenBankingGateway. It is a sample bank  implementing the XS2A in production environment.

### HBCI-Server

The server implementing the HBCI interface which defines transmission protocol, messages formats and security procedures for home banking
and is used to in the project when the HBCI-protocol is chosen. The HBCI-Server manages the different rules described by the HTC-Protocol when the Bank choses to
support this protocol.

### Fintech-Server

The Fintech-Server is an example of FinTech-Application that can get user data (accounts and transactions) provided from the OpenBankingGateway. This server simply represents the data provider 
for the FinTech UI which is responsible to get user data from the OpenBankingGateway for FinTech-UI where the user can sees his data and also initiate a payment.

### Fintech-UI

The FinTech-UI is an Angular web application that can get user data (accounts and transactions) from the FinTech-Server and show to the user. On FibnTech-UI user can also initiate a payment 
and send to the FinTech-Server that can proceed it and send to the OpenBankingGate that validates the payment and sends the result back FinTech-Server which sends back to FinTech-UI. 

### Consent-UI

The Consent-UI is an Angular web application uses by FinTech-UI to hold a digital consent by providing TAN that enables the FinTech-UI to hold the digital consent which will authorize the 
user to see his accounts and transactions and also to initiate a payment.
