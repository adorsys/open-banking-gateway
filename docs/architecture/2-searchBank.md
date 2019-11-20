# PSU Searches Bank By Keyword

## Definition
Describes the bank search functionality in a FinTech Application. Generally bank search is provided to prevent PSU from manually entering complicated bank identifiers.

This bank search API allow for incremental keyword based search.

## Use Case Steps
Use cases for this API:
1. FinTechSearchUI displays a search screen to the PSU
2. PSU enters any keyword in the search input field
3. FinTechSearchUI forward request to TppBeanSearchAPI
4. TppBankSearchApi returns a list of matching BankDescriptors to FinTechBankSearchUI
5. UI react on every keyboard and interactively displays a matching list to the PSU

## Implementation Approaches
We will distinguish between remote and local incremental search.

### Remote Incremental Search
The remote incremental search is implemented on the server side. A rest endpoint receives a searchString and return a list of matching search entries.

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/2-searchBank.puml&fmt=svg&vvv=1&sanitize=true)

### Local Incremental Search
Local incremental search generally provides a way of reducing round trip to servers. So there is no local incremental search endpoint, but an endpoint to download the list of all BankDescriptors.

The client is then responsible for the indexing and the implementation of the search logic.   

For the purpose of keeping the client code simple, interface will also provide the possibility of downloading a standard lucene index file, as many platform provide login provide processing of lucene indexes. This approach will prevent each client from perfoming the expensive lucene indexing.

The following diagram describes additional steps performed in the local incremental search.

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/2a-searchBankLocal.puml&fmt=svg&vvv=1&sanitize=true)

Like this diagram shown, local bank search does not send request to the network.

#### Discriminator
A discriminator can be used to limit the size of the index returned to client. If for example the TPP does not support some banks, there is no need for returning those banks to the client.

#### LuceneSearch
This is the UI-Komponent that operates on a lucene search index in the UI-Application.
