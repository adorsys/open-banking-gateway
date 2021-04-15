package de.adorsys.opba.smartanalytics;

import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;
import de.adorsys.opba.smartanalytics.dto.AnalyticsRequest;
import org.springframework.stereotype.Service;

@Service
public class EmbeddedSmartAnalyticsAnalyzer extends TransactionAnalyzer {

    @Override
    AnalyticsResult analyze(AnalyticsRequest request) {
        return null;
    }
}
