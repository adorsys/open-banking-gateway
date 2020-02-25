package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.transactions;

import de.adorsys.opba.protocol.xs2a.service.mapper.PathQueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aResourceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aTransactionParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import org.springframework.stereotype.Service;

@Service
public class TransactionListingServiceExtractor extends PathQueryHeadersMapperTemplate<
        TransactionListXs2aContext,
        Xs2aResourceParameters,
        Xs2aTransactionParameters,
        Xs2aWithConsentIdHeaders> {

    public TransactionListingServiceExtractor(
            DtoMapper<Xs2aContext, Xs2aWithConsentIdHeaders> toHeaders,
            DtoMapper<TransactionListXs2aContext, Xs2aResourceParameters> toPath,
            DtoMapper<TransactionListXs2aContext, Xs2aTransactionParameters> toQuery) {
        super(toHeaders, toPath, toQuery);
    }
}
