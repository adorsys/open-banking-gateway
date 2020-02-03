package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionsResponseMapper {
    TransactionsResponse map(de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse transactionsResponse);
}
