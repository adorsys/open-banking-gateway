package de.adorsys.opba.tppbankingapi.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = Const.API_MAPPERS_PACKAGE)
public interface TransactionsFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<TransactionsResponse, TransactionsResponseBody> {
    TransactionsResponse map(TransactionsResponseBody facadeEntity);
}
