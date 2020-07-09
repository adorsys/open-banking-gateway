package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionListXs2aContext extends Xs2aContext {

    private String iban;
    private String resourceId;
    private String currency;

    private String bookingStatus;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
