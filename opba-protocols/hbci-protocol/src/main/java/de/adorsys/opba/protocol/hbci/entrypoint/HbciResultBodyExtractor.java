package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.multibanking.domain.BalancesReport;
import de.adorsys.multibanking.domain.BankAccount;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListDetailBody;
import de.adorsys.opba.protocol.api.dto.result.body.Amount;
import de.adorsys.opba.protocol.api.dto.result.body.Balance;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PisSinglePaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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

    private final HbciAccountsToFacadeMapper accountsToFacadeMapper;
    private final HbciTransactionsToFacadeMapper transactionsToFacadeMapper;
    private final HbciPaymentToFacadeMapper paymentToFacadeMapper;

    public AccountListBody extractAccountList(ProcessResponse result) {
        AisListAccountsResult accountsResult = (AisListAccountsResult) result.getResult();
        return accountsToFacadeMapper.map(accountsResult);
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result) {
        AisListTransactionsResult transactionsResult = (AisListTransactionsResult) result.getResult();
        return transactionsToFacadeMapper.map(transactionsResult);
    }

    public SinglePaymentBody extractSinglePaymentBody(ProcessResponse result) {
        return paymentToFacadeMapper.map((PisSinglePaymentResult) result.getResult());
    }

    public PaymentStatusBody extractPaymentStatusBody(ProcessResponse result) {
        return paymentToFacadeMapper.mapStatus((PaymentInitiateBody) result.getResult());
    }

    public PaymentInfoBody extractPaymentInfoBody(ProcessResponse result) {
        return paymentToFacadeMapper.mapInfo((PaymentInitiateBody) result.getResult());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE, uses = {HbciToAccountBodyMapper.class})
    public interface HbciAccountsToFacadeMapper {

        AccountListBody map(AisListAccountsResult accountList);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciToAccountBodyMapper {

        @Mapping(
                expression = "java(com.google.common.base.Strings.isNullOrEmpty(account.getIban()) ? account.getAccountNumber() : account.getIban())",
                target = "resourceId"
        )
        @Mapping(source = "accountNumber", target = "bban")
        @Mapping(source = "balances", target = "balances", qualifiedByName = "hbciBalancesToProtocolBalances")
        AccountListDetailBody map(BankAccount account);


        @Named("hbciBalancesToProtocolBalances")
        static List<Balance> hbciBalancesToProtocolBalances(BalancesReport hbciBalances) {
            List<Balance> protocolBalances = new ArrayList<>();
            if (hbciBalances != null) {
                add(protocolBalances, "available", hbciBalances.getAvailableBalance());
                add(protocolBalances, "credit", hbciBalances.getCreditBalance());
                add(protocolBalances, "ready", hbciBalances.getReadyBalance());
                add(protocolBalances, "unready", hbciBalances.getUnreadyBalance());
                add(protocolBalances, "used", hbciBalances.getUsedBalance());
            }
            return protocolBalances;
        }

        static void add(List<Balance> protocolBalances, String type, de.adorsys.multibanking.domain.Balance hbciBalance) {
            if (hbciBalance != null) {
                protocolBalances.add(Balance.builder()
                    .balanceType(type)
                    .lastChangeDateTime(hbciBalance.getDate().atStartOfDay(ZoneId.of("Europe/Berlin")).toOffsetDateTime())
                    .balanceAmount(Amount.builder().amount(hbciBalance.getAmount().toString()).currency(hbciBalance.getCurrency()).build())
                    .build());
            }
        }


    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciPaymentToFacadeMapper {

        @Mapping(source = "transactionId", target = "paymentId")
        SinglePaymentBody map(PisSinglePaymentResult paymentResult);

        @Mapping(source = "paymentStatus", target = "transactionStatus")
        PaymentStatusBody mapStatus(PaymentInitiateBody paymentResult);

        @Mapping(source = "paymentStatus", target = "transactionStatus")
        PaymentInfoBody mapInfo(PaymentInitiateBody paymentResult);
    }
}
