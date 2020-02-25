package de.adorsys.opba.tppbankingapi.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = Const.API_MAPPERS_PACKAGE)
public interface FacadeToRestMapper extends FacadeToRestMapperBase<AccountList, AccountListBody> {
    AccountList mapFromFacadeToRest(AccountListBody facadeEntity);
}

