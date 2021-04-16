package de.adorsys.opba.smartanalytics;

import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;
import de.adorsys.opba.smartanalytics.dto.AnalyticsRequest;
import de.adorsys.smartanalytics.core.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddedSmartAnalyticsAnalyzer extends TransactionAnalyzer {

    private final SmartAnalyticsMapper mapper;
    private final AnalyticsService smartAnalytics;

    @Override
    AnalyticsResult analyze(AnalyticsRequest request) {
        var mappedRequest = mapper.map(request);
        mappedRequest.setCustomRules(List.of()); // Builder prevents usage of AfterMapping
        var analyzed = smartAnalytics.analytics(mappedRequest);
        return mapper.map(analyzed);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.SMARTANALYTICS_MAPPERS_PACKAGE)
    public interface SmartAnalyticsMapper {

        @Mapping(source = "transactions", target = "bookings")
        de.adorsys.smartanalytics.api.AnalyticsRequest map(AnalyticsRequest request);

        AnalyticsResult map(de.adorsys.smartanalytics.api.AnalyticsResult result);
    }
}
