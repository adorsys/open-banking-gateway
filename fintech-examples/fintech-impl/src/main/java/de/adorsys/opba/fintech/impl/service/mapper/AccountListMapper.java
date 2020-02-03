package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import org.mapstruct.Mapper;

@Mapper
public interface AccountListMapper {
    AccountList map(de.adorsys.opba.tpp.ais.api.model.generated.AccountList accountList);
}
