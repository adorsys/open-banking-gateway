package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = "de.adorsys.opba.fintech.impl.mapper.generated")
public interface TransactionsResponseMapper {
    TransactionsResponse mapFromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse transactionsResponse);
}
