package de.adorsys.opba.smartanalytics;

import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;
import de.adorsys.opba.smartanalytics.dto.AnalyticsRequest;
import de.adorsys.smartanalytics.api.SmartAnalyticsFacade;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmbeddedSmartAnalyticsAnalyzer extends TransactionAnalyzer {

    private final SmartAnalyticsMapper mapper;
    private final SmartAnalyticsFacade smartAnalytics;

    @Override
    AnalyticsResult analyze(AnalyticsRequest request) {
        var analyzed = smartAnalytics.analyzeBookings(mapper.map(request));
        return mapper.map(analyzed);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.SMARTANALYTICS_MAPPERS_PACKAGE)
    public interface SmartAnalyticsMapper {

        @Mapping(source = "transactions", target = "bookings")
        de.adorsys.smartanalytics.api.AnalyticsRequest map(AnalyticsRequest request);

        AnalyticsResult map(de.adorsys.smartanalytics.api.AnalyticsResult result);
    }
}
