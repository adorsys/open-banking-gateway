package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = "de.adorsys.opba.protocol.xs2a.entrypoint.generated")
public interface XS2aToFacadeMapper {
    AccountListBody mapFromXs2aToFacade(AccountListHolder accountList);
}
