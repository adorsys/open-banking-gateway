package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import org.mapstruct.Mapper;

@Mapper
public interface AccountListMapper {
    AccountList mapFromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountList accountList);
}
