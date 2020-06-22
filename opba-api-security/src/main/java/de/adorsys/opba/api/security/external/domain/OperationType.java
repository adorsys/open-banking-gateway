package de.adorsys.opba.api.security.external.domain;

import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

public enum OperationType {
    AIS,
    PIS,
    BANK_SEARCH,
    CONFIRM_CONSENT;

    public static boolean isTransactionsPath(String path) {
        return path.contains("/transactions");
    }

    public static boolean isBankSearchPath(String path) {
        return path.contains("/bank-search");
    }

    public static boolean isPaymentInfo(HttpServletRequest request) {
        return HttpMethod.GET.name().equals(request.getMethod());
    }
}
