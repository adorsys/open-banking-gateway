package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.multibanking.domain.BankAccount;
import de.adorsys.multibanking.domain.Booking;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListDetailBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountReport;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

/**
 * Extracts HBCI result from ASPSP response and does initial translation to Banking protocol facade native object
 * for transactions or accounts list.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HbciResultBodyExtractor {

    private final HbciToFacadeMapper mapper;

    public AccountListBody extractAccountList(ProcessResponse result) {
        AisListAccountsResult accountsResult = (AisListAccountsResult) result.getResult();
        return mapper.map(accountsResult);
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result) {
        AisListTransactionsResult transactionsResult = (AisListTransactionsResult) result.getResult();
        return mapper.map(transactionsResult);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE, uses = {HbciToAccountBodyMapper.class, HbciToTransactionBodyMapper.class})
    public interface HbciToFacadeMapper {

        AccountListBody map(AisListAccountsResult accountList);

        @Mapping(source = "bookings", target = "transactions")
        TransactionsResponseBody map(AisListTransactionsResult transactionsResult);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciToAccountBodyMapper {
        @Mapping(source = "iban", target = "resourceId")
        AccountListDetailBody map(BankAccount accountList);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciToTransactionBodyMapper {

        default AccountReport map(List<Booking> bookings) {
            AccountReport.AccountReportBuilder report = AccountReport.builder();
            List<Booking> booked = bookings.stream().filter(it -> null != it.getValutaDate()).collect(Collectors.toList());
            List<Booking> pending = bookings.stream().filter(it -> null == it.getValutaDate()).collect(Collectors.toList());
            return report
                    .booked(booked)
                    .pending(pending)
                    .build();
        }

        @Mapping(source = "externalId", target = "transactionId")
        @Mapping(source = "bookingDate", target = "bookingDate")
        @Mapping(source = "valutaDate", target = "valueDate")
        @Mapping(expression = "java(new de.adorsys.opba.protocol.api.dto.result.body.Amount(booking.getCurrency(), booking.getAmount().toString()))",
                target = "transactionAmount")
        @Mapping(source = "otherAccount", target = "debtorAccount")
        TransactionDetailsBody map(Booking booking);
    }
}
