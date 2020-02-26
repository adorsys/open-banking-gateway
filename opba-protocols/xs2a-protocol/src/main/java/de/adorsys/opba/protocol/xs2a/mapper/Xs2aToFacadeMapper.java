package de.adorsys.opba.protocol.xs2a.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.Transactions;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(implementationPackage = "de.adorsys.opba.protocol.xs2a.mapper.generated")
public interface Xs2aToFacadeMapper {
    AccountListBody map(AccountListHolder accountList);
    TransactionListBody map(List<Transactions> transactions);
}
