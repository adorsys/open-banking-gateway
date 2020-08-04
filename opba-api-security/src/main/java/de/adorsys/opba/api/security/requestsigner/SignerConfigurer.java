package de.adorsys.opba.api.security.requestsigner;

import de.adorsys.opba.api.security.generator.api.GeneratedSigner;

/**
 * Configures OpenBanking request signer generation that can be used by FinTech and is used to validate signature.
 */
// Resources are provided by maven-remote-resources plugin
@GeneratedSigner(
        signerClassName = "OpenBankingSigner",
        signatureHeaderName = "X-Request-Signature",
        openApiYamlPath = {
                "static/tpp_banking_api_ais.yml",
                "static/tpp_banking_api_pis.yml",
                "static/tpp_banking_api_bank_search.yml"
        })
@SuppressWarnings("unused") // Is used to configure generation of request Signer - OpenBankingSigner
public class SignerConfigurer {
}

