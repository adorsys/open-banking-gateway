package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.multibanking.domain.Booking;
import de.adorsys.opba.protocol.api.dto.result.body.AccountReport;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

@Service
@RequiredArgsConstructor
public class HbciTransactionsToFacadeMapper {

    private final HbciToTransactionBodyMapper mapper;

    public TransactionsResponseBody map(AisListTransactionsResult transactionsResult) {
        TransactionsResponseBody.TransactionsResponseBodyBuilder response = TransactionsResponseBody.builder();
        if (null != transactionsResult.getBookings()) {
            response.transactions(mapBookings(transactionsResult.getBookings()));
        }
        return response.build();
    }

    private AccountReport mapBookings(List<Booking> bookings) {
        AccountReport.AccountReportBuilder report = AccountReport.builder();
        TransactionListBody booked = new TransactionListBody();
        TransactionListBody pending = new TransactionListBody();

        bookings.stream()
                .filter(it -> null != it.getValutaDate())
                .map(mapper::map)
                .forEach(booked::add);

        bookings.stream()
                .filter(it -> null == it.getValutaDate())
                .map(mapper::map)
                .forEach(pending::add);


        return report
                .booked(booked)
                .pending(pending)
                .build();
    }


    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciToTransactionBodyMapper {

        @Mapping(source = "externalId", target = "transactionId")
        @Mapping(source = "bookingDate", target = "bookingDate")
        @Mapping(source = "valutaDate", target = "valueDate")
        @Mapping(expression = "java(de.adorsys.opba.protocol.api.dto.result.body.Amount.builder().currency(booking.getCurrency()).amount(booking.getAmount().toString()).build())",
                target = "transactionAmount")
        @Mapping(source = "otherAccount", target = "debtorAccount")
        @Mapping(expression = "java(null != booking.getUsage() ? booking.getUsage() : booking.getText())", target = "remittanceInformationUnstructured")
        TransactionDetailsBody map(Booking booking);
    }
}
