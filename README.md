[![Build Status](https://github.com/adorsys/open-banking-gateway/workflows/Develop%20branch%20build/badge.svg)](https://github.com/adorsys/open-banking-gateway/actions)

[![Heavy tests status](https://github.com/adorsys/open-banking-gateway/workflows/Develop%20branch%20heavy%20tests%20daily%20build/badge.svg)](https://github.com/adorsys/open-banking-gateway/actions)

[![Gitter](https://badges.gitter.im/adorsys/open-banking-gateway.svg)](https://gitter.im/adorsys/open-banking-gateway?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

**Code coverage**
 - Backend: [![codecov-backend](https://codecov.io/gh/adorsys/open-banking-gateway/branch/develop/graph/badge.svg?flag=backend)](https://codecov.io/gh/adorsys/open-banking-gateway)
 - Frontend: [![codecov-frontend](https://codecov.io/gh/adorsys/open-banking-gateway/branch/develop/graph/badge.svg?flag=frontend)](https://codecov.io/gh/adorsys/open-banking-gateway)
 - Example code: [![codecov-examples](https://codecov.io/gh/adorsys/open-banking-gateway/branch/develop/graph/badge.svg?flag=fintech)](https://codecov.io/gh/adorsys/open-banking-gateway)

This is the try-out version of adorsys Open Banking Gateway: an open source (AGPL v3) solution to get acquainted with the adorsys-developed gateway and test its AIS and PIS flows and the respective connectivity to the largest retail banks in Germany (see our XS2A [Adapter](https://github.com/adorsys/xs2a-adapter) project).

If you are looking for a strong base framework to build up your own gateway capabilities, we would be thrilled to cooperate with you and share our know-how of the framework and overall open finance and development expertise.

If you are an organisation that would like to commercially use our solutions beyond AGPL v3 requirements, we are open to discuss alternative individual licensing options. If you are interested in working with us or have any other inquiries, please contact us under [psd2@adorsys.com](mailto:psd2@adorsys.com).

# Open Banking Gateway
Provides tools, adapters and connectors for transparent access to open banking apis. The initial effort focuses on the connectivity to banks that implement the European PSD2 directive either through one of the common market initiatives like : [The Berlin Group NextGenPSD2](https://www.berlin-group.org/psd2-access-to-bank-accounts), [The Open Banking UK](https://www.openbanking.org.uk/), [The Polish PSD2 API](https://polishapi.org/en/) or even through proprietary bank api like  [the ING’s PSD2 API](https://developer.ing.com/openbanking/).

The idea is to help the banking sector, and the TTPs to make the functioning smooth and receive better customer feedback for the services provided. As a full-service provider, we take care of a technological platform and infrastructure, dedicated project management team, as well as direct access to industry cooperation with dominant market participants.

##### Drivng payement transformations

Our finServices offer platform APIs and functionality for 
- [API Management](https://adorsys.com/en/expertise/api-management/)
- [Identity & Access Management](https://adorsys.com/en/expertise/identity-accessmanagement/)
- [Multibanking/PSD2](https://adorsys.com/en/expertise/open-banking-psd2/)
- [Smartanalytics /AI](https://adorsys.com/en/expertise/smartanalytics-a-i/)
- [Data Security](https://adorsys.com/en/expertise/data-security/)

#####Professional Services
Covering the entire process of digital transformation, our Professional Services can also look after your
- [Business Design](https://adorsys.com/en/services/business-design/`) 
- [Business Software Development](https://adorsys.com/en/services/software-development/)
- [Mobile Development](https://adorsys.com/en/services/mobile-development/)
- [Conversational UI](https://adorsys.com/en/services/conversational-interfaces/)
- [DevOps](https://adorsys.com/en/services/devops-services/)

## What this Project is about

### Tackle the Key Challenge for Third Party Providers of Payment Services
The European PSD2 as the first regulator driven Open Banking initiative offers many opportunities for both banks, known as traditional provider of payment services (called ASPSPs in this context) and other Third Party Providers of payment services (TPPs). TPPs can use account information and payment services provided by banks to offer new innovative services to bank account holders. The more banks and TPPs can interact with each other, the more payment account holders can be provided with reacher banking solutions, which in turn simplifies and leverage commercial value chains.

Being able to interact with different banking APIs can be a time and cost consuming challenge. Even though the PSD2 requires European banks to provide APIs and despite the effort of market initiatives to provide common standard interfaces, there is still a multitude of divergent authorization schemes involved and a lot of space for implementation options. A bank can even decide not to join one of the known market initiatives and define it's own PSD2 compliant API. 

The purpose of this open banking gateway is to provide the community with a common and simple interface for accessing major Open Banking APIs.

### Introducing the FinTech as a Major Role
Being a regulator driven initiative, PSD2 mandates the regulation of TPPs. With this additional detail, the market is experiencing a distinction between regulated TPPs and non regulated FinTechs. This framework is therefore designed taking in consideration the existence of the category of payment service providers called FinTech that used APIs exposed by a regulated TPP to access payment services exposed by banks.

### Address Security Issues associated with PSU Access to Multiple Interfaces
In the Open Banking Context, a payment service user (PSU or banking account holder) might have to deal with up to 3 different user interface to initiate, authorize and get the requested banking service executed. There being redirected back and forth from one UserAgent (resp. device) to another. This intensive use of redirection in Open Banking bearing a lot of risk of impersonating the PSU, we set a goal of this Framework to dissect the complexity involved with those redirection processes and open forums for discussion of possible solutions and sample implementations. Following papers are the first attempt to capture the problem ([UserAgent Redirection](docs/architecture/concepts/psu-device-redirection.md), [PSU Access Security Design](docs/architecture/concepts/psu-security-concept.md)).  
 
 
## Project Demo
 
 - [Demo deployment and guide](docs/demo_env.md)

## Big Picture

The following picture displays the overall architecture of this banking gateway:
![High level architecture](docs/img/big-picture.png)


## Security concept

The following picture displays the overall security concept of this banking gateway:
![Security concept](docs/img/security-concept.png)

Security concept has 2 kinds of flows:
  - authenticated (for consent sharing)
  - anonymous (for payments, but can be authenticated too). 

Here are detailed diagrams of each flow:
 - [Authenticated security concept detailed flow](docs/img/security-details/authenticated-security-concept-details.png)
 - [Anonymous security concept detailed flow](docs/img/security-details/anonymous-security-concept-details.png)

## Technical architecture

The following picture displays the overall technical architecture concept of this banking gateway:
![Technical architecture](docs/img/technical-architecture.svg)

**Key components as shown on diagram**:

**APIs:**
 - [Banking API](opba-banking-rest-api) and its implementation [Banking API Impl](opba-banking-rest-impl)
 - [Consent API](opba-consent-rest-api) and its implementation [Consent API Impl](opba-consent-rest-impl)

 **Facade:**
 - [Banking protocol facade](opba-banking-protocol-facade)
 
 **Protocol:**
  - [Banking protocol API](opba-protocols/opba-protocol-api)
  - [XS2A compliant banking protocol Impl](opba-protocols/xs2a-protocol)


## Running the project locally

 - [docker-compose-dev.yml](https://github.com/adorsys/open-banking-gateway/tree/develop/docker-compose-dev.yml) - docker-compose file in the project root for **Development** (requires building docker images)
 - [docker-compose.yml](https://github.com/adorsys/open-banking-gateway/tree/develop/docker-compose.yml) - docker-compose file in the project root for **Demo** (Images will be pulled from DockerHub)

## Postman scripts to play with API 

- [postman-collections](postman/collections)
- [postman-environments](postman/environments) (for playing use [this Postman environment](https://github.com/adorsys/open-banking-gateway/tree/develop/postman/environments/OPBA-DEV-NO-SIG.postman_environment.json))

### Postman collection details

- [postman-ais-collection](https://github.com/adorsys/open-banking-gateway/tree/develop/postman/collections/OPBA-AIS-HBCI-OR-XS2A-EMBEDDED-2-SCA-METHODS.postman_collection.json) Xs2a-embedded or HBCI AIS (account information services) example - getting users' account and transactions list

**Note:** Postman requires disabled request signing functionality - for that use Spring-profile `no-signature-filter`.
You can use our DEV environment (without signature check) if you import [this Postman environment](https://github.com/adorsys/open-banking-gateway/tree/develop/postman/environments/OPBA-DEV-NO-SIG.postman_environment.json)


## Information for developers:
 
 - Working with BPMN: As most protocols use BPMN, we have developed 
 the [plugin 'Flowable BPMN visualizer'](https://plugins.jetbrains.com/plugin/14318-flowable-bpmn-visualizer) that directly integrates into IntelliJ
 with code navigation, refactoring and other stuff for **Flowable BPMN engine**. It will make your work a lot easier
 as you don't need to leave IntelliJ to change diagram or to see what class is used at which step.
 
 - Running local tests faster. To avoid Postgres TestContainer slow initialization one can use following environment variables:
 `TESTCONTAINERS_REUSE_ENABLE=true;TESTCONTAINERS_RYUK_DISABLED=true;SPRING_DATASOURCE_URL=jdbc:tc:postgresql:12:////open_banking?TC_DAEMON=true&?TC_TMPFS=/testtmpfs:rw&TC_INITSCRIPT=init.sql&TC_REUSABLE=true`
  this will keep Postgres TestContainer started and migrated after 1st run, significantly reducing next tests startup time.
  Note, that it will introduce shared state across tests, which is mostly OK but for some tests may cause failures.

 - Starting with project: [How to start with project](https://github.com/adorsys/open-banking-gateway/tree/develop/how-to-start-with-project) 
 
 - Populating database with bank data: [How to fill database with bank data](https://github.com/adorsys/open-banking-gateway/tree/develop/opba-db/README.md) 

## Documentation

Please take a look into our [**documentation**](https://adorsys.github.io/open-banking-gateway/doc/develop/) to know more about:

### Planned and released versions

* [Versioning, Release and Support policy](docs/version_policy.md)
* [Release notes](docs/releasenotes.md) 
* [Roadmap for next features development](docs/roadmap.md)

### Architecture 
* [Dictionary](docs/architecture/dictionary.md)
* [Use Cases](docs/architecture/use_cases.md)
* [Banking Protoсol Design](docs/architecture/technical-details.md)
* [User Agent Redirection](docs/architecture/concepts/psu-device-redirection.md)
* [PSU Access Security Design](docs/architecture/concepts/psu-security-concept.md)
* [JavaDoc](https://adorsys.github.io/open-banking-gateway/javadoc/latest/index.html)

## Third Parties Contribution

This project is designed to enable contribution from different sources, as the open banking challenge will start with a magnitude of discrepancies in individual bank implementations, even for banks implementing a common standards.

### How to contribute

* [Getting started](docs/getting_started.md)
* [Contribution Guidelines](docs/ContributionGuidelines.md) 
 

## Authors & Contact

* **[Francis Pouatcha](mailto:fpo@adorsys.de)** - *Initial work* - [adorsys](https://www.adorsys.de)

See also the list of [contributors](https://github.com/adorsys/open-banking-gateway/graphs/contributors) who participated in this project.

For commercial support please contact **[adorsys Team](https://adorsys.de/)**.


## License

This project is licensed under Affero GNU General Public License v.3 (AGPL v.3) - see the [LICENSE](LICENSE) file for details.

For alternative individual licensing options please contact us at [psd2@adorsys.com](mailto:psd2@adorsys.com).