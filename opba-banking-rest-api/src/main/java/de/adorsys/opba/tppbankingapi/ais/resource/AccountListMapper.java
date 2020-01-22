package de.adorsys.opba.tppbankingapi.ais.resource;

import de.adorsys.opba.protocol.services.ais.account.AccountsReport;
import de.adorsys.opba.tppbankingapi.ais.model.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.AccountStatus;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = TppBankingApiAisResource.class)
public interface AccountListMapper {
    AccountList toAccountList(AccountsReport accountsReport);

    default AccountStatus toAccountStatus(String status) {
        return AccountStatus.valueOf(StringUtils.upperCase(status));
    }
}
