# Select Bank

General terms defined in the [dictionary](dictionary.md)

## Definition
The bank selection allows to download the [BankProfile](dictionary.md#BankProfile) of a bank and cache it in the consent session for reuse while processing the PSU request. It can also be used to display bank details to the PSU at selection.

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/gh-pages/docs/architecture/diagrams/useCases/3-selectBank.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Case Steps
Use cases for this API:
### SelBnk-001 FinTechUI.selectBank
PSU selects a bank from the list of banks displayed by the FinTechUI
### SelBnk-002 FinTechApi.loadBankProfile
FinTechUI sends a load loadBankProfile request to FinTechAPI passing the bankId
### SelBnk-003 TppBeanSearchApi.loadBankProfile
FinTechAPI sends a load loadBankProfile request to TppBeanSearchApi passing the bankId
### SelBnk-004 & 005 Return 200_BankProfile
TppBankSearchApi returns the BankProfine object matching the given bankId.
### SelBnk-005 FinTechUI.displayBankProfile
The displayed bank profile also displays banking api services offered by the selected bank.