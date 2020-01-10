# PSU Searches Bank By Keyword

## Definition
Describes the bank search functionality in a FinTech Application. Generally bank search is provided to prevent PSU from manually entering complicated bank identifiers.

This bank search API allows for incremental keyword based search.

## Implementation Approaches
We will distinguish between remote and local incremental search.

## Remote Incremental Search
The remote incremental search is implemented on the server side. A rest endpoint receives a keyword and return a list of matching search entries.

### Use Case Steps
Use cases for this API:
1. PSU loads FinTechSearchScreen
2. FinTechUI displays a search screen to the PSU
3. PSU enters any keyword in the search input field
4. FinTechUI forward request to FinTechAPI
5. FinTechAPI forward request to TppBeanSearchApi
6. TppBankSearchApi returns a list of matching BankDescriptors to FinTechAPI
7. FinTechAPI returns a list of matching BankDescriptors to FinTechBankSearchUI
8. UI displays list of found bank descriptors to PSU

Step 4. through 8. is repeated as long as PSU modifies keywords (by adding or removing characters)

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/2-searchBank.puml&fmt=svg&vvv=1&sanitize=true)

#### RemoteSearch-001, -002 FinTechUI.loadFinTechSeachScreen, displaySearchScreen

#### RemoteSearch-003 FinTechUI.enterSearchString

#### RemoteSearch-004 FinTechApi.bankSearch
The FinTechUI sends a get request to the FinTechApi. Request contains
* keyword (searchInput): The bank search input string
* start (searchStartIndex): The index of the first result
* max (searchMaxResult): the max number of entries to return with the response.
* sessionState: provided as a path param. Used to read the FinTechLoginSessionCookie. 
* FinTechLoginSessionCookie: provided in the Cookie header. Used to identify the PSU.

#### RemoteSearch-005, -006 TppBankSearchApi.bankSearch
The FinTechApi sends a get request to the TppBankSearchApi. Request contains
* keyword (searchInput): The bank search input string
* start (searchStartIndex): The index of the first result
* max (searchMaxResult): the max number of entries to return with the response.

TppBankSearchApi returns a search result with following information.
* List<BankProfile>: found entries.
* searchInput: The bank search input string (might have been normalized in the backend).
* searchStartIndex: The index of the first result
* searchMaxResult: The max number of entries to return with the response.
* searchTotalResult: The total count of entities found for the given search input.

#### RemoteSearch-007 FinTechApi return 200_BankSearchResult
Result returned to FinTechUI contains same information as in RemoteSearch-005 and addition session management information like:
* sessionState: provided in the response body. Used read the FinTechLoginSessionCookie. 
* FinTechLoginSessionCookie: provided in the Set-Cookie header.

#### RemoteSearch-007 FinTechUI.displaySearchResult
Finally, the FinTechUI display's the bank search result to the PSU.

## Local Incremental Search
Local incremental search generally provides a way of reducing round trip to servers. So there is no local incremental search endpoint, but an endpoint to download the list of all BankProfiles.

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
6. FinTechUI displays a search screen to the PSU
7. PSU enters any keyword in the search input field
8. FinTechUI call the search routine of the embedded LocalSearch
9. LocalSearch uses the keyword to retrieve the list of matching entries
10. LocalSearch returns a list of matching BankDescriptors to FinTechBankSearchUI
11. UI displays list of found bank descriptors to PSU

Step 8. through 11. is repeated as long as PSU modifies keywords (by adding or removing characters)

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/2a-searchBankLocal.puml&fmt=svg&vvv=1&sanitize=true)

Like this diagram shows, local bank search does not send request to the network.

#### LocalSearch-001 FinTechUI.loadFinTechSeachScreen
PSU loads the FinTechSearchString of the FinTechUI.

#### LocalSearch-101, -103 FinTechApi.bankSearch
The FinTechUI sends a get request to the FinTechApi. If the List<BankProfile> is not yet loaded by the FinTechUI, the FinTechUI issues a bankSearch request to the FinTechApi. A keyword passed as query parameter allows the backend to reduce the initial extent of records included in the index.


* keyword (searchInput): The bank search input string. Must be used to reduce the extent of records to be included in the local search. E.g.: c=DE could limit the search to bank in Germany.
* start (searchStartIndex): The index of the first result. The start will be set to 0 to get all records.
* max (searchMaxResult): the max number of entries to return with the response. Set this number to -1 to receive all applicable records from the server.
* sessionState: provided as a path parameter. Used to read the FinTechLoginSessionCookie. 
* FinTechLoginSessionCookie: provided in the Cookie header. Used to identify the PSU.

TppBankSearchApi returns a search result with following information.
* List<BankProfile>: found entries.
* searchInput: The bank search input string (might have been normalized in the backend).
* searchStartIndex: The index of the first result
* searchMaxResult: The max number of entries to return with the response.
* searchTotalResult: The total count of entities found for the given search input.


#### LocalSearch-102, -103 TppBankSearchApi.bankSearch
The FinTechApi sends a get request to the TppBankSearchApi. Request contains
* keyword (searchInput): The bank search input string
* start (searchStartIndex): The index of the first result
* max (searchMaxResult): the max number of entries to return with the response.

TppBankSearchApi returns a search result with following information.
* List<BankProfile>: found entries.
* searchInput: The bank search input string (might have been normalized in the backend).
* searchStartIndex: The index of the first result
* searchMaxResult: The max number of entries to return with the response.
* searchTotalResult: The total count of entities found for the given search input.


#### LocalSearch-104 FinTechApi return 200_BankSearchResult
Result returned to FinTechUI contains same information as in LocalSearch-103 and addition session management information like:
* sessionState: provided in the response body. Used read the FinTechLoginSessionCookie. 
* FinTechLoginSessionCookie: provided in the Set-Cookie header.

#### LocalSearch-105 displaySearchScreen

#### LocalSearch-106 .. 110 enterKeyword, LocalSearch.search
As the PSU enters search keywords, the FinTechUI issues a search request to the LocalSearch that performs the search operation locally and returns corresponding result that is displayed to the PSU on the go.

### New Idioms

#### Discriminator
The searchInput object passed to FinTechApi in this case is called discriminator and is used to reduce the extent of records to be included in the local search. E.g.: c=DE could limit the search to bank in Germany. If for example the TPP does not support some banks, he can include that information in the searchInput-String.

#### LocalSearch
This is the UI-Komponent that operates (like lucene) on a local search index in the UI-Application. The technology used to implement this search is specific to the UI technology. JavaScript based technologies might use tools like: [elasticlunr](https://github.com/weixsong/elasticlunr.js)

#### Caching List<BankProfile>
The List<BankProfile> return for the purpose of a local search can be cached by the FinTechApi to reduce quantity of request issued to the TppBankSearchApi 
