package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.context;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;


@Data
@ToString(callSuper = true)
public class TransactionListXs2aContextLog extends Xs2aContextLog {

    private String iban;
    private String resourceId;
    private String currency;
    private String bookingStatus;
    private LocalDate dateFrom;
    private LocalDate dateTo;

}
