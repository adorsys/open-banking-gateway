package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = "de.adorsys.opba.protocol.api.dto.result.body.generated")
public interface FacadeToProtocolMapper {
    AccountList mapFromFacadeToProtocol(AccountListBody facadeEntity);
}

