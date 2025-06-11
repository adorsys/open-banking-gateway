package de.adorsys.opba.api.security.requestsigner;

import de.adorsys.opba.api.security.generator.api.GeneratedDataToSignNormalizer;

/**
 * Configures OpenBanking request to canonical string generation that can be used by FinTech and is used to validate signature.
 */
// Resources are provided by maven-remote-resources plugin
@GeneratedDataToSignNormalizer(
        signerClassName = "OpenBankingDataToSignProvider",
        signatureHeaderName = "X-Request-Signature",
        openApiYamlPath = {
            "static/tpp_banking_api_ais.yml",
            "static/tpp_banking_api_pis.yml",
            "static/tpp_banking_api_bank_search.yml",
            "static/tpp_banking_api_bank_info.yml",
            "static/tpp_banking_api_token.yml"
        })
@SuppressWarnings("unused") // Is used to configure generation of request Signer - OpenBankingDataToSignProvider
public class DataToSignNormalizerConfigurer {
}

