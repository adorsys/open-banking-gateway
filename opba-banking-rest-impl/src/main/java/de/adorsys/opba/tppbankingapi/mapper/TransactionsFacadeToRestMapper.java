package de.adorsys.opba.tppbankingapi.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.xs2a.adapter.service.model.Transactions;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = Const.API_MAPPERS_PACKAGE)
public interface TransactionsFacadeToRestMapper extends FacadeToRestMapper<Transactions, TransactionListBody> {
    Transactions mapFromFacadeToRest(TransactionListBody facadeEntity);
}

