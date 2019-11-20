# PSU Searches Bank By Keyword

## Definition
Describes the bank search functionality in a FinTech Application. Generally bank search is provided to prevent PSU from manually entering complicated bank identifiers.

This bank search API allow for incremental keyword based search.

## Implementation Approaches
We will distinguish between remote and local incremental search.

## Remote Incremental Search
The remote incremental search is implemented on the server side. A rest endpoint receives a searchString and return a list of matching search entries.

### Use Case Steps
Use cases for this API:
1. PSU loads FinTechSearchScreen
2. FinTechSearchUI displays a search screen to the PSU
3. PSU enters any keyword in the search input field
4. FinTechSearchUI forward request to FinTechAPI
5. FinTechAPI forward request to TppBeanSearchApi
6. TppBankSearchApi returns a list of matching BankDescriptors to FinTechAPI
7. FinTechAPI returns a list of matching BankDescriptors to FinTechBankSearchUI
8. UI displays list of found bank descriptors to PSU

Step 4. through 8. is repeated as long as PSU modifies keywords (by adding or removing characters)

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/2-searchBank.puml&fmt=svg&vvv=1&sanitize=true)

## Local Incremental Search
Local incremental search generally provides a way of reducing round trip to servers. So there is no local incremental search endpoint, but an endpoint to download the list of all BankDescriptors.

The client is then responsible for the indexing and the implementation of the search logic.   

For the purpose of keeping the client code simple, interface will also provide the possibility of downloading a standard lucene index file, as many platform provide login provide processing of lucene indexes. This approach will prevent each client from perfoming the expensive lucene indexing.

The following diagram describes additional steps performed in the local incremental search.

### Use Case Steps
Use cases for this API:
1. PSU loads FinTechSearchScreen
2. FinTechUI requests the BankSearchIndex from FinTechApi
3. FinTechApi requests the BankSearchIndex from TppBeanSearchApi
4. TppBeanSearchApi return BankSearchIndex to FinTechApi
5. FinTechApi return BankSearchIndex to FinTechUI
6. FinTechSearchUI displays a search screen to the PSU
7. PSU enters any keyword in the search input field
8. FinTechSearchUI call the search routine of the embedded LuceneSearch
9. LuceneSearch uses the keyword to retrieve the list of matching entries
10. LuceneSearch returns a list of matching BankDescriptors to FinTechBankSearchUI
11. UI displays list of found bank descriptors to PSU

Step 8. through 11. is repeated as long as PSU modifies keywords (by adding or removing characters)

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/2a-searchBankLocal.puml&fmt=svg&vvv=1&sanitize=true)

Like this diagram shown, local bank search does not send request to the network.

### New Idioms

#### Discriminator
A discriminator can be used to limit the size of the index returned to client. If for example the TPP does not support some banks, there is no need for returning those banks to the client.

#### LuceneSearch
This is the UI-Komponent that operates on a lucene search index in the UI-Application.
