package de.adorsys.opba.protocol.api.dto.result.body;

import org.mapstruct.Mapper;

@Mapper(implementationPackage = FacadeToProtocolMapper.RESULT_BODY_GENERATED)
public interface FacadeToProtocolMapper {

    String RESULT_BODY_GENERATED = "de.adorsys.opba.protocol.api.dto.result.body.generated";

    AccountList mapFromFacadeToProtocol(AccountListBody facadeEntity);
}

