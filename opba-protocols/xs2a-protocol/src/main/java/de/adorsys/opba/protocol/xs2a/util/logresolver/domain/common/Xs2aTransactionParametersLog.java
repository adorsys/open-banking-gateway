package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.common;

import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;


@Data
@ToString(callSuper = true)
public class Xs2aTransactionParametersLog extends Xs2aWithBalanceParametersLog implements NotSensitiveData {

    private String bookingStatus;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public String getNotSensitiveData() {
        return "Xs2aTransactionParametersLog("
                + ")";
    }
}
