package de.adorsys.opba.tppbankingapi.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionList;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = Const.API_MAPPERS_PACKAGE)
public interface TransactionsFacadeResponseBodyToRestBodyMapper extends FacadeResponseMapper.FacadeResponseBodyToRestBodyMapper<TransactionList, TransactionListBody> {
    TransactionList map(TransactionListBody facadeEntity);
}
