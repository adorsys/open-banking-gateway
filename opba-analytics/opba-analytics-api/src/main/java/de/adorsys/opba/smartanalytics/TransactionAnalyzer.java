package de.adorsys.opba.smartanalytics;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.api.services.ResultBodyPostProcessor;
import de.adorsys.opba.smartanalytics.dto.AnalyticsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static de.adorsys.opba.smartanalytics.GlobalConst.ANALYZER_MAPPERS_PACKAGE;

public abstract class TransactionAnalyzer implements ResultBodyPostProcessor {

    protected static final TransactionBodyMapper MAPPER = Mappers.getMapper(TransactionBodyMapper.class) ;

    @Override
    public void apply(Object requestMappedResult) {
        var body = (TransactionsResponseBody) requestMappedResult;
        if (null == body.getTransactions()) {
            return;
        }

        analyze(AnalyticsRequest.builder().transactions(MAPPER.map()).build());

    }

    @Override
    public boolean shouldApply(FacadeServiceableRequest request, Object requestMappedResult) {
        return requestMappedResult instanceof TransactionsResponseBody;
    }

    abstract AnalyticsResult analyze(AnalyticsRequest request);

    @Mapper(implementationPackage = ANALYZER_MAPPERS_PACKAGE)
    public interface TransactionBodyMapper {

        TransactionDetailsBody map(TransactionDetailsBody body);
    }
}
