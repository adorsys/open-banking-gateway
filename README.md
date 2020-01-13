# Open Banking Gateway
Provides tools, adapters and connectors for transparent access to open banking apis. The initial effort focuses on the connectivity to banks that implement the European PSD2 directive either through one of the common market initiatives like :

* [The Berlin Group NextGenPSD2](https://www.berlin-group.org/psd2-access-to-bank-accounts)
* [The Open Banking UK](https://www.openbanking.org.uk/)
* [The Polish PSD2 API](https://polishapi.org/en/)

Or even through proprietary bank api like 

* [the ING’s PSD2 API](https://developer.ing.com/openbanking/)

## What this Project is about

### Key Challenge for a TPPs

PSD2 as the first regulatory driven Open Banking initiative offers many opportunities for both Banks and Third Party Providers. TPPs can use the account information and payment services provided by the banks in order to offer new innovative services to the end users. The more banks a TPP can interact with each other, the more end consumer can be provided with reacher banking solutions, which in turn simplifies and leverage commercial value chains.  
Being able to interact with different bank APIs can be a time and cost consuming challenge. Even though PSD2 sets a standard for bank interfaces, there is a lot of space for implementation options. A bank, therefore, can have it's own PSD2 compliant solution or implement one of the known market standards, like Open Banking UK, Berlin Group or STET. This open banking gateway will provide with a common interface with the aim of simplifying access to those APIs by third parties. 

### Third Parties Contribution

This project is designed to enable contribution from different sources, as the open banking challenge will start with a magnitude of discrepancies in individual bank implementations, even for banks implementing a common standards.

The following picture displays the overall architecture of this banking gateway:
![High level architecture](docs/img/open-banking-gateway-arch-13-01-2020.png)

## Releases and versions

* [Versioning, Release and Support policy](docs/version_policy.md)
* [Release notes](docs/releasenotes.md) 
* [Roadmap for next features development](docs/roadmap.md)
* [Initial Requirements](docs/architecture/drafts/initial_requirements.md)

 
## Authors & Contact

* **[Francis Pouatcha](mailto:fpo@adorsys.de)** - *Initial work* - [adorsys](https://www.adorsys.de)

See also the list of [contributors](https://github.com/adorsys/open-banking-gateway/graphs/contributors) who participated in this project.

For commercial support please contact **[adorsys Team](https://adorsys.de/en/psd2)**.

## License

This project is licensed under the Apache License version 2.0 - see the [LICENSE](LICENSE) file for details

## Checkstyle

The code uses <code>backend.checkstyle.xml</code> placed in the root of the project. To activate it with intellij 
install the <code>CheckStyle-IDEA v5.34.0</code> Plugin and configure in <code>Other Settings -> Checkstyle</code>
the version <code>8.19</code> and add the configuration file <code>backend.checkstyle.xml</code>.

## open api

The code generated from
<pre>
opba-consent-rest-api/src/main/resources/static/consent_api.yml
opba-banking-rest-api/src/main/resources/static/banking_api_ais.yml
</pre>
is generated to
<pre>
opba-consent-rest-api/target/generated-sources/open-api/src/main/java
opba-banking-rest-api/target/generated-sources/open-api/src/main/java
</pre>

So the folders have to be made known to the ide.

## Dictionary
* [Dictionary](docs/architecture/dictionary.md)

## Contribution Guidelines

* [Contribution Guidelines](docs/ContributionGuidelines.md) 

## JavaDoc
* [JavaDoc](https://adorsys.github.io/open-banking-gateway/javadoc/latest/index.html)

## Use Cases
* [Use Cases](docs/architecture/uses_cases.md)
