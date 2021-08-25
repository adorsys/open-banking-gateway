package de.adorsys.opba.protocol.api.common;

/**
 * Defines expected result body format in getTransactions. For now xs2a-adapter parses only JSON.
 * In case of another format [de.adorsys.xs2a.adapter.api.AccountInformationService.getTransactionListAsString] will be used
 * and String result will be additionally parsed in OBG
 */
public enum ResultContentType {
    JSON,
    XML
}
