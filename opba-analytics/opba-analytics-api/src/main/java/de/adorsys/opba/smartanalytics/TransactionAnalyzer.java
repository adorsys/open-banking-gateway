package de.adorsys.opba.smartanalytics;

import de.adorsys.opba.protocol.api.services.ResultBodyPostProcessor;
import de.adorsys.opba.smartanalytics.dto.AnalyticsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;

public interface TransactionAnalyzer extends ResultBodyPostProcessor {

    AnalyticsResult analyze(AnalyticsRequest request);
}
