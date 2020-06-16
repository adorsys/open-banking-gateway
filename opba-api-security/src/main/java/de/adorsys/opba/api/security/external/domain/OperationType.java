package de.adorsys.opba.api.security.external.domain;

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

    public static boolean isGetPaymentStatus(String path) {
        return path.contains("/status");
    }

    public static boolean isGetPayment(String method) {
        return "GET".equals(method);
    }
}
