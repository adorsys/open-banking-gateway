package de.adorsys.opba.protocol.xs2a.tests;

import lombok.Value;

@Value
public class GetTransactionsQueryParams {
    String dateFrom;
    String dateTo;
    String entryReferenceFrom;
    String bookingStatus;
    String deltaList;

    public static GetTransactionsQueryParams newEmptyInstance() {
        return new GetTransactionsQueryParams(null, null, null, null, null);
    }
}
