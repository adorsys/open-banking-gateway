# Open Banking Gateway Project

## containers

### Postgres

Postgres is used as database by all servers and should be started first in order to register all servers.

### Open-Banking-Gateway Server

The OpenBankingGateway is a web service that provide following list of services in an uniform interface for the different banks: Ability to list user's accounts with / without balances, transactions and also make payments. It also provides an uniform REST interface for payment and consent authorization. Currently only the XS2A and HBCI protocols are supported.

### HBCI-Server

The HBCI(sandbox) is a sample bank implementing the HBCI protocol.

### Fintech-Server

The Fintech server is the web service that communicates with the Fintech-UI. It provides the user with a bank search functionality that allows him to select his bank. It is also responsible for providing information on the user's data (accounts and transactions) or for initiating a payment. However, this is only possible if the user has already given his consent to these actions. The Fintech server must delegate all the work to the open bank gateway server, so that he can first grant the consent before receiving information about its accounts and / or transactions.

### Fintech-UI

The Fintech UI is an Angular web application that is used as a kind of user account manager. It allows the user to select his bank, retrieve all information about his accounts and transactions in the selected bank, and also process a payment transaction.

### Consent-UI

The Content-UI is an Angular web application used to authorize a consent for a specific bank. The user can grant consent either for all his accounts or for some of them by entering the IBAN of these accounts. Depending on the type of bank the user uses, this consent can be used to retrieve information about both the user account and transactions, or it can be used for only the account or the transactions
