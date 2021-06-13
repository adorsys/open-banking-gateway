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

/**
 * The request by FinTech to list PSUs' transactions on some account.
 */
// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionsRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * ASPSP account ID to list transactions on.
     */
    private String accountId;

    /**
     * Transactions starting date from.
     */
    private LocalDate dateFrom;

    /**
     * Transactions ending date to.
     */
    private LocalDate dateTo;

    private String entryReferenceFrom;

    /**
     * Transactions booking status - i.e. BOTH or BOOKED.
     */
    private String bookingStatus;

    private Boolean deltaList;

    /**
     * Result page number.
     */
    private Integer page;

    /**
     * Records per page.
     */
    private Integer pageSize;

    /**
     * Additional (protocol-customary) request parameters.
     */
    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
