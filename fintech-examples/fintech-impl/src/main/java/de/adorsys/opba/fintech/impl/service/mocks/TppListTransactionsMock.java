package de.adorsys.opba.fintech.impl.service.mocks;

import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;

public class TppListTransactionsMock extends MockBaseClass {
    public TransactionsResponse getTransactionsResponse() {
        return GSON.fromJson(readFile("MOCK_TPP_LIST_TRANSACTIONS.json"), TransactionsResponse.class);
    }
}
