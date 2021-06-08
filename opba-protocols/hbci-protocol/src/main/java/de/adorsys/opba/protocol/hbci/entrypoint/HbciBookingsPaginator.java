package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.multibanking.domain.Booking;
import de.adorsys.opba.protocol.api.dto.result.body.Paging;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@Component
public class HbciBookingsPaginator {

    public List<Booking> getTransactionsPage(List<Booking> transactionsList, Paging paging) {
        Collections.sort(transactionsList, (Booking o1, Booking o2) -> {
            LocalDate o1LocalDate = o1.getValutaDate() == null
                    ? o1.getBookingDate()
                    : null;

            LocalDate o2LocalDate = o2.getValutaDate() == null
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
