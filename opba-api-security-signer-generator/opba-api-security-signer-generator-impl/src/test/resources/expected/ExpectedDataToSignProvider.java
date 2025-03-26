package de.adorsys.signer.test;

import de.adorsys.opba.api.security.generator.api.DataToSignProvider;
import de.adorsys.opba.api.security.generator.api.MatcherUtil;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import jakarta.annotation.Generated;
import java.lang.Override;
import java.lang.String;

@Generated(value = "de.adorsys.opba.api.security.generator.normalizer.DataToSignProviderGenerator", comments = "This class provides request signature canonicalization classes (convert Request to String to sign)")
public class TestSigner implements DataToSignProvider {
    private final String basePath;

    public TestSigner() {
        this.basePath = "";
    }

    /**
     * @param basePath Server base path (requested path is relative to it)
     */
    public TestSigner(String basePath) {
        this.basePath = basePath;
    }

    /**
     * @param basePath Server base path (requested path is relative to it)
     */
    @Override
    public DataToSignProvider withBasePath(String basePath) {
        return new TestSigner(basePath);
    }

    /**
     * @param toSign The request to sign
     */
    @Override
    public RequestDataToSignNormalizer normalizerFor(RequestToSign toSign) {
        String computedPath = toSign.getPath().substring(basePath.length());
        if (DataToSignProvider.HttpMethod.GET == toSign.getMethod() && MatcherUtil.matches("/v1/banking/ais/accounts/{account-id}/transactions", computedPath)) {
            return new GetTransactions();
        }
        throw new IllegalStateException(String.format("Can\'t create signer for %s %s (full path: %s)", toSign.getMethod(), computedPath, toSign.getPath()));
    }
}