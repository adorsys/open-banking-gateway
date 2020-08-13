# What is this

This is the default implementation of XS2A protocol.

# Technical description

Application request-response flow uses message like pattern - on each layer resulting outcome is 
translated to the message(dto) that is native for current layer. This diagram shows the entire
flow (idealized) from the protocol internals to endpoint output:

![Result flow by layers](docs/result-mappings.svg)


**Output class designation**

| Layer (bottom-up)      | Output class                   | Used for                                                                                                                                                  |
|------------------------|--------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Xs2A protocol internal | ProcessResponse                | Return response from ASPSP i.e. Account list                                                                                                              |
| Xs2A protocol internal | Redirect                       | Require to redirect user somewhere                                                                                                                        |
| Xs2A protocol internal | ConsentAcquired                | Notify that consent was acquired and the user should be redirected elsewhere                                                                              |
| Xs2A protocol internal | ValidationProblem              | Notify that to proceed with consent authorization user input is needed and the user should be redirected to the screen where missing data can be provided |
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

Such flow allows application to perform dialog between PSU, FinTech and underlying protocol.
In the essence, everything is based on the loop of 202 response codes indicating that PSU needs to be redirected somewhere to
input some parameters. When 202 loop ends - user is redirected back to the FinTech and FinTech can read the
account list - the presence of account list will be indicated by response code 200. 

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


# How to start local environment

## 1. Run/Create postgres docker container for banking protocol

```shell script
docker run --rm  --name pg-docker -e POSTGRES_PASSWORD=docker -e POSTGRES_DB=open_banking -d -p 5432:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data   postgres --max_prepared_transactions=100
```
Run [init.sql](core/banking-protocol/src/test/resources/init.sql) on it using user `postgres` and password `docker`
to create schema banking_protocol and text-extensions.

!TODO: This file is on develop branch.

## 2. Start Sandbox (all defaults)

Execute `BasicTest.testEnvStartsUp()` as java test or from maven - it will create everything it needs including UI

## 3. Start banking-protocol 

Run BankingProtocol spring application.
And you are good to go. Use localhost:8080 to access banking-protocol.


# How to design BPMN flow with Flowable

## Using Flowable docker all image:

1. This will start Flowable applications on port `8080`:
`docker run -p 8080:8080 flowable/all-in-one`
1. Open Flowable modeler:
`http://localhost:8080/flowable-modeler` (credentials are `admin/test`) 

## Coding guideline

1. Each task MUST have concrete context class referenced by variable name `CONTEXT`. This variable is used as 
input and output of task, sub-task, activity. This is done to enforce type safety and to avoid issues of dealing 
with string literals declared both in XML and Java code. 
Additionally it allows us to handle some concurrency issues and to trace data flow in code.
I.e. the only proper way to communicate between processes is:
```xml
<callActivity id="consentCreationActivity" name="consentCreationActivity" calledElement="createConsent" flowable:calledElementType="key" flowable:inheritVariables="true" flowable:fallbackToDefaultTenant="false">
  <extensionElements>
    <flowable:out source="CONTEXT" target="CONTEXT"></flowable:out>
  </extensionElements>
</callActivity>
```
Note `CONTEXT` in above snippet. 

!TODO: Add XSLT validator for that.

## Protocol customization

To customize beans that are getting called in XS2A-protocol (for example to implement bank protocol that calls some 
custom beans, but follows XS2A flow) one can check [xs2a-sandbox-protocol](../xs2a-sandbox-protocol) directory.
Customization is done by replacing bean names in generated xml files.

## IntelliJ and language injection

### Navigation between code and BPMN diagram

To work with BPMN diagrams efficiently within IntelliJ, you need to configure language injections.
`Settings > Editor > Language Injections` -> Click import and select [language-injections.xml](docs/language-injections.xml)
This will add Ctrl-click navigation between your classes and BPMN diagrams as well as IntelliJ will allow smart refactoring.

### Working with generated XML diagrams:

1. Ensure that the directory with generated xml is not excluded. If is -> `Right click on directory` -> `Mark Directory As` -> `Not Excluded`.
![Unexclude folder](docs/unexclude_dir.gif)