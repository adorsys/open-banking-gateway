package de.adorsys.opba.protocol.api.common;

/**
 * Defines expected result body format in getTransactions. For now xs2a-adapter parses only JSON.
 * In case of another format [de.adorsys.xs2a.adapter.api.AccountInformationService.getTransactionListAsString] will be used
 * and String result will be additionally parsed in OBG
 */
public enum ResultContentType {
    JSON("application/json"),
    XML("application/xml");

    private final String value;

    ResultContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
