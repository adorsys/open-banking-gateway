package de.adorsys.signer.test;

import de.adorsys.opba.api.security.generator.api.RequestDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import jakarta.annotation.Generated;
import java.lang.Override;
import java.lang.String;

@Generated(
        value = "de.adorsys.opba.api.security.generator.normalizer.DataToSignProviderGenerator",
        comments = "This class provides request signature canonicalization functionality for a concrete request (convert Request to String to sign)"
)
public class GetTransactions implements RequestDataToSignNormalizer {
    /**
     * @param toSign Request data to sign
     */
    @Override
    public String canonicalString(RequestToSign toSign) {
        StringBuilder result = new StringBuilder();
        // Add path;
        if (null == toSign.getPath() || "".equals(toSign.getPath())) {
            throw new IllegalStateException("Missing path");
        }
        result.append(toSign.getPath()).append("&");
        // Done adding path;
        // Add headers;
        // Done adding headers;
        // Add query parameters;
        // Optional parameter dateFrom;
        String dateFrom = toSign.getQueryParams().get("dateFrom");
        if (null != dateFrom && !"".equals(dateFrom)) {
            result.append("dateFrom").append("=").append(dateFrom).append("&");
        }
        // Optional parameter dateTo;
        String dateTo = toSign.getQueryParams().get("dateTo");
        if (null != dateTo && !"".equals(dateTo)) {
            result.append("dateTo").append("=").append(dateTo).append("&");
        }
        // Done adding query parameters;
        return result.toString();
    }
}