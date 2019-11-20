# PSU Searches Bank By Keyword

## Definition
Describes the bank search functionality in a bank application.

The Purpose of that is to have  a uniform API to allow a PSU to find his bank by keyword and display the list of banks found. 

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/feature/normalize_usecases/docs/architecture/diagrams/useCases/2-searchBank.puml&fmt=svg&vvv=1&sanitize=true)  


## Use Case Steps
Use cases for this API:
1. FinTechSearchUI displays a search screen to the PSU
2. PSU enters any keyword in the search input field
3. FinTechSearchUI forward request to TppBeanSearchAPI
4. TppBankSearchApi returns a list of matching BankDescriptors to FinTechBankSearchUI
5. UI react on every keyboard and interactively displays a matching list to the PSU

