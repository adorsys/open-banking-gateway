# Technical description

## Layered architecture

Application uses layered architecture that is composed of:

1. REST API layer (Banking, Consent, Authentication). Responsible for consuming requests from external services.
1. Protocol Facade. Responsible for protocol selection to serve the request, protocol result processing and encryption.
1. Protocol layer. Modular layer that contains protocol implementation. Protocol is responsible to execute actual
requests to ASPSP.

The following picture represents them in graphical form:
![Technical architecture](../img/technical-architecture.svg)

## Bottom-up layers communication
Application request-response flow uses message like pattern - on each layer resulting outcome is 
translated to the message(dto) that is native for current layer. This diagram shows the entire
flow (idealized) from the protocol internals to endpoint output:

![Result flow by layers](diagrams/components/ideal-result-mappings.svg)

Such flow allows application to perform dialog between PSU, FinTech and underlying protocol.
In the essence, everything is based on the loop of 202 response codes indicating that PSU needs to be redirected somewhere to
input some parameters. When 202 loop ends - user is redirected back to the FinTech and FinTech can read the
account list - the presence of account list will be indicated by response code 200. 

**Output class designation**

| Layer (bottom-up)      | Output class                   | Used for                                                                                                                                                  |
|------------------------|--------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| XS2A protocol API      | SuccessResult                  | Return response from protocol containing a useful payload i.e. Account list                                                                               |
| XS2A protocol API      | ErrorResult                    | Error result that should be reported by redirecting the  user somewhere                                                                                   |
| XS2A protocol API      | ValidationErrorResult          | Notify that some input is required from user and user should be redirected to the page where he can input missing data                                    |
| XS2A protocol API      | ConsentAcquiredResult          | Notify that consent was acquired and user should be redirected somewhere                                                                                  |
| XS2A protocol API      | AuthorizationRequiredResult    | Notify that consent was missing and to get new consent PSU authorization is required                                                                      |
| XS2A protocol API      | AuthorizationDeniedResult      | During consent authorization either user or 3rd party decided to deny consent                                                                             |
| Protocol facade        | FacadeSuccessResult            | Response with useful payload (i.e. users' account list)                                                                                                   |
| Protocol facade        | FacadeErrorResult              | Fatal error result, no redirect possible                                                                                                                  |
| Protocol facade        | FacadeStartAuthorizationResult | Consent missing and authorization session to perform new consent authorization was created                                                                |
| Protocol facade        | FacadeRedirectResult           | PSU (or FinTech is required to provide PSU and redirect him) required to be redirected somewhere                                                          |
| Protocol facade        | FacadeRedirectErrorResult      | Some error has happened, but the redirection is required to handle it                                                                                     |
| API                    | Response entity, code 200      | Response with useful payload                                                                                                                              |
| API                    | Response entity, code 202      | Redirection is required                                                                                                                                   |
| API                    | Response entity, code 3xx      | Automatic web browser redirection                                                                                                                         |

For example, the one of the commonly observed pattern for AIS list accounts service will be:

1. FinTech calls OpenBanking REST API listAccounts endpoint
1. The request is propagated to XS2A protocol
1. XS2A protocol deduces that consent is missing and replies with **AuthorizationRequiredResult** or **ValidationErrorResult**
1. The outcome of XS2A protocol is translated to **202 accepted-redirection** for consent authorization
1. FinTech receives 202 accepted with Location header pointing where it needs to bring PSU
1. PSU enters consent authorization UI
1. PSU provides the required parameters 
1. Protocol responds with **ValidationErrorResult** to users' input
1. User is being redirected to next parameter input page using **202 accepted-redirection**
1. User provides more parameters
1. Protocol responds with **ConsentAcquired** or **Redirect** result
1. User is being redirected back to FinTech
1. FinTech calls OpenBanking REST API listAccounts endpoint
1. The request is propagated to XS2A protocol
1. XS2A protocol calls the ASPSP and gets account list, returns **SuccessResult**
1. Fintech receives Response entity with status code 200 and account list