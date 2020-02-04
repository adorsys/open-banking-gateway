package de.adorsys.opba.fintech.impl.mapper.generated;

import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionsResponseMapper {
    TransactionsResponse mapFromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse transactionsResponse);
}
