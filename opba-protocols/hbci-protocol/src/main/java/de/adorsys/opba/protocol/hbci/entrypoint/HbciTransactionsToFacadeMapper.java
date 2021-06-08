package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.multibanking.domain.Booking;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
import de.adorsys.opba.protocol.api.dto.result.body.AccountReport;
import de.adorsys.opba.protocol.api.dto.result.body.Paging;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

@Service
@RequiredArgsConstructor
public class HbciTransactionsToFacadeMapper {

    private final HbciToTransactionBodyMapper mapper;
    private final HbciBookingsPaginator paginator;

    public TransactionsResponseBody map(AisListTransactionsResult transactionsResult, ServiceContext<ListTransactionsRequest> context) {
        TransactionsResponseBody.TransactionsResponseBodyBuilder response = TransactionsResponseBody.builder();

        if (null != transactionsResult.getBookings()) {
            Paging paging = Paging.builder()
                    .page(context.getRequest().getPage())
                    .perPage(context.getRequest().getPerPage())
                    .pageCount((int) Math.ceil(transactionsResult.getBookings().size() / context.getRequest().getPerPage()))
                    .totalCount(transactionsResult.getBookings().size())
                    .build();

            List<Booking> bookings = paginator.getTransactionsPage(transactionsResult.getBookings(), paging);

            if (null != bookings) {
                response.transactions(mapBookings(bookings, context));
            }

            response.paging(paging);
        }
        return response.build();
    }

    private AccountReport mapBookings(List<Booking> bookings, ServiceContext<ListTransactionsRequest> context) {
        AccountReport.AccountReportBuilder report = AccountReport.builder();
        TransactionListBody booked = new TransactionListBody();
        TransactionListBody pending = new TransactionListBody();

        AccountReference ourAccount = AccountReference.builder()
            .iban(context.getRequest().getAccountId())
            .currency("EUR")
            .build();

        bookings.stream()
                .filter(it -> null != it.getValutaDate())
                .map((Booking booking) -> mapper.map(booking, ourAccount))
                .forEach(booked::add);

        bookings.stream()
                .filter(it -> null == it.getValutaDate())
                .map((Booking booking) -> mapper.map(booking, ourAccount))
                .forEach(pending::add);


        return report
                .booked(booked)
                .pending(pending)
                .build();
    }


    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciToTransactionBodyMapper {

        @Mapping(source = "booking.externalId", target = "transactionId")
        @Mapping(source = "booking.bookingDate", target = "bookingDate")
        @Mapping(source = "booking.valutaDate", target = "valueDate")
        @Mapping(expression = "java(de.adorsys.opba.protocol.api.dto.result.body.Amount.builder().currency(booking.getCurrency()).amount(booking.getAmount().toString()).build())",
                target = "transactionAmount")
        @Mapping(source = "booking.otherAccount", target = "debtorAccount")
        @Mapping(source = "booking.otherAccount", target = "creditorAccount")
        @Mapping(expression = "java(null != booking.getUsage() ? booking.getUsage() : booking.getText())", target = "remittanceInformationUnstructured")
        TransactionDetailsBody map(Booking booking, AccountReference ourAccount);

        @AfterMapping
        default void update(@MappingTarget TransactionDetailsBody.TransactionDetailsBodyBuilder transactionDetailsBody, Booking booking, AccountReference ourAccount) {

            if (booking.getAmount() != null && booking.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                transactionDetailsBody.debtorAccount(ourAccount);
            } else {
                transactionDetailsBody.creditorAccount(ourAccount);
            }
        }
    }
}
