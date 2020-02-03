package de.adorsys.opba.fintech.impl.service.mocks;

import de.adorsys.opba.tpp.ais.api.model.generated.AccountReport;

public class TppListTransactionsMock extends MockBaseClass {
    public AccountReport getTransactionList() {
        return GSON.fromJson(readFile("TPP_LIST_TRANSACTIONS.json"), AccountReport.class);
    }
}
