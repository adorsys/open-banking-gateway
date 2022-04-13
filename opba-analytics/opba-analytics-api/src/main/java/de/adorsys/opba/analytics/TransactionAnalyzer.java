package de.adorsys.opba.analytics;

import de.adorsys.opba.protocol.api.dto.request.Analytics;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.api.services.ResultBodyPostProcessor;
import de.adorsys.opba.analytics.dto.AnalyticsRequest;

public abstract class TransactionAnalyzer implements ResultBodyPostProcessor {

    @Override
    public Object apply(Object requestMappedResult) {
        var body = (TransactionsResponseBody) requestMappedResult;
        if (null == body.getTransactions()) {
            return body;
        }

        var result = body.toBuilder();
        if (null != body.getTransactions().getBooked()) {
            var analyzedTxn = analyze(AnalyticsRequest.builder().transactions(body.getTransactions().getBooked()).build());
            result = result.analytics(analyzedTxn);
        }

        return result.build();
    }

    @Override
    public boolean shouldApply(FacadeServiceableRequest request, Object requestMappedResult) {
        return request.getWithAnalytics() == Analytics.OWN && requestMappedResult instanceof TransactionsResponseBody;
    }

    public abstract AnalyticsResult analyze(AnalyticsRequest request);
}
