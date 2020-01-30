package de.adorsys.opba.protocol.api.dto.request.transactions;

import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

// TODO Validation
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionsRequest implements FacadeServiceableGetter {

    private FacadeServiceableRequest facadeServiceable;

    private String accountId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String entryReferenceFrom;
    private String bookingStatus;
    private Boolean deltaList;

    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
