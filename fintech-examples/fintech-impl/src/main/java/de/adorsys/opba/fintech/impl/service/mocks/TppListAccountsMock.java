package de.adorsys.opba.fintech.impl.service.mocks;

import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TppListAccountsMock extends MockBaseClass {
    public AccountList getAccountList() {
        return GSON.fromJson(readFile("TPP_LIST_ACCOUNTS.json"), AccountList.class);
    }
}
