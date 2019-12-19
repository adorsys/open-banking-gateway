# Select Bank

General terms defined in the [dictionary](dictionary.md)

## Definition
The bank selection allows to download the [BankProfile](dictionary.md#BankProfile) of a bank and cache it in the consent session for reuse while processing the PSU request. It can also be used to display bank details to the PSU at selection.

## Diagram

![Session diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/develop/docs/architecture/diagrams/useCases/3-selectBank.puml&fmt=svg&vvv=1&sanitize=true)  

## Use Case Steps
Use cases for this API:
1. PSU selects a bank from the list of banks displayed by the FinTechUI
2. FinTechUI sends a load loadBankProfile request to FinTechAPI passing the bankId
3. FinTechAPI sends a load loadBankProfile request to TppBeanSearchApi passing the bankId
4. TppBankSearchApi returns the BankProfine object matching the given bankId.

The displayed bank profile also displays banking api services offered by the selected bank.
