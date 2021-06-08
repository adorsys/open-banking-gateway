package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.Paging;
import de.adorsys.xs2a.adapter.api.model.Transactions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


public class Xs2aTransactionsPaginator {

    public List<Transactions> getTransactionsPage(List<Transactions> transactionsList, Paging paging) {
        Collections.sort(transactionsList, (Transactions o1, Transactions o2) -> {
            LocalDate o1LocalDate = o1.getValueDate() == null
                    ? o1.getBookingDate()
                    : null;

            LocalDate o2LocalDate = o2.getValueDate() == null
                    ? o2.getBookingDate()
                    : null;

            if (o1LocalDate == null || o2LocalDate == null) {
                return o1LocalDate == null ? -1 : 1;
            }

            return o1LocalDate.compareTo(o2LocalDate);
        });

        int fromIndex = paging.getPage() * paging.getPerPage();
        int toIndex = paging.getPage() * paging.getPerPage();

        return transactionsList.subList(
                fromIndex,
                toIndex > paging.getTotalCount() ? paging.getTotalCount() : toIndex);
    }
}
