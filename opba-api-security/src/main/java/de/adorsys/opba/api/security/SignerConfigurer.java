package de.adorsys.opba.api.security;

import de.adorsys.opba.api.security.generator.api.GeneratedSigner;

// Resources are provided by maven-remote-resources plugin
@GeneratedSigner(openApiYamlPath = {
        "static/tpp_banking_api_ais.yml",
        "static/tpp_banking_api_pis.yml",
        "static/tpp_banking_api_bank_search.yml"
})
public class SignerConfigurer {
}

