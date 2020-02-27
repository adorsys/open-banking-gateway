package de.adorsys.opba.protocol.xs2a.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = "de.adorsys.opba.protocol.xs2a.mapper.generated")
public interface Xs2aToFacadeMapper {
    AccountListBody map(AccountListHolder accountList);
    TransactionsResponseBody map(TransactionsReport transactions);
}
