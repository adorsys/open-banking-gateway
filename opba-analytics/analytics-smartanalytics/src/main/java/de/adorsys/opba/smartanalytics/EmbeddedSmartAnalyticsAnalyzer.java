package de.adorsys.opba.smartanalytics;

import de.adorsys.opba.protocol.api.dto.result.body.AnalyticsResult;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.smartanalytics.dto.AnalyticsRequest;
import de.adorsys.smartanalytics.api.Booking;
import de.adorsys.smartanalytics.core.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddedSmartAnalyticsAnalyzer extends TransactionAnalyzer {

    private final SmartAnalyticsMapper mapper;
    private final AnalyticsService smartAnalytics;

    @Override
    AnalyticsResult analyze(AnalyticsRequest request) {
        var mappedRequest = mapper.map(request);
        mappedRequest.setCustomRules(List.of()); // Builder prevents usage of AfterMapping
        try {
            var analyzed = smartAnalytics.analytics(mappedRequest);
            return mapper.map(analyzed);
        } catch (RuntimeException ex) {
            log.error("Failed to obtain analytics result", ex);
        }

        return null;
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = GlobalConst.SMARTANALYTICS_MAPPERS_PACKAGE)
    public interface SmartAnalyticsMapper {

        @Mapping(source = "transactions", target = "bookings")
        de.adorsys.smartanalytics.api.AnalyticsRequest map(AnalyticsRequest request);

        AnalyticsResult map(de.adorsys.smartanalytics.api.AnalyticsResult result);

        @Mapping(source = "transactionId", target = "bookingId")
        @Mapping(source = "valueDate", target = "executionDate")
        @Mapping(source = "remittanceInformationUnstructured", target = "purpose")
        @Mapping(source = "transactionAmount.amount", target = "amount")
        Booking map(TransactionDetailsBody body);

        @AfterMapping
        default void update(TransactionDetailsBody source, @MappingTarget Booking target) {
            if (target.getAmount() != null && BigDecimal.ZERO.compareTo(target.getAmount()) > 0) { // 0 is bigger than amount
                target.setAccountNumber(source.getCreditorName());
                target.setIban(source.getCreditorAccount() != null ? source.getCreditorAccount().getIban() : null);
            } else {
                target.setAccountNumber(source.getDebtorName());
                target.setIban(source.getDebtorAccount() != null ? source.getDebtorAccount().getIban() : null);
            }
        }
    }
}
