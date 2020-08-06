package de.adorsys.opba.protocol.hbci.service.protocol.pis;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankAccount;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.PaymentStatusResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.multibanking.domain.transaction.PaymentStatusReqest;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.service.consent.HbciScaRequiredUtil;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

@Service("hbciPaymentStatusExecutor")
@RequiredArgsConstructor
public class HbciPaymentStatus extends ValidatedExecution<PaymentHbciContext> {
    private final OnlineBankingService onlineBankingService;
    private final HbciPaymentStatus.PaymentStatusMapper paymentStatusMapper;

    @Override
    protected void doRealExecution(DelegateExecution execution, PaymentHbciContext context) {
        HbciConsent consent = context.getHbciDialogConsent();
        PaymentStatusReqest paymentStatusReqest = paymentStatusMapper.map(context.getPayment());
        BankAccount account = new BankAccount();
        account.setIban(context.getAccountIban());
        paymentStatusReqest.setPsuAccount(account);

        BankAccess bankAccess = new BankAccess();
        bankAccess.setHbciPassportState(context.getHbciPassportState());
        TransactionRequest<PaymentStatusReqest> request = create(paymentStatusReqest, new BankApiUser(),
                bankAccess, context.getBank(), consent);
        PaymentStatusResponse response = onlineBankingService.getStrongCustomerAuthorisation().getPaymentStatus(request);
        boolean postScaRequired = HbciScaRequiredUtil.extraCheckIfScaRequired(response);

        if (null == response.getAuthorisationCodeResponse() && !postScaRequired) {
            ContextUtil.getAndUpdateContext(
                    execution,
                    (PaymentHbciContext ctx) -> {
                        ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                        ctx.getPayment().setPaymentStatus(response.getPaymentStatus().toString());
                        ctx.setTanChallengeRequired(false);
                    }
            );

            return;
        }

        if (null != response.getAuthorisationCodeResponse()) {
            onlineBankingService.getStrongCustomerAuthorisation().afterExecute(consent, response.getAuthorisationCodeResponse());
        }
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                    ctx.setTanChallengeRequired(true);
                }
        );
    }

    public <T extends AbstractTransaction> TransactionRequest<T> create(T transaction,
                                                                    BankApiUser bankApiUser,
                                                                    BankAccess bankAccess,
                                                                    Bank bank,
                                                                    Object bankApiConsentData) {
        TransactionRequest<T> transactionRequest = new TransactionRequest<>(transaction);
        transactionRequest.setBankApiUser(bankApiUser);
        transactionRequest.setBankAccess(bankAccess);
        transactionRequest.setBankApiConsentData(bankApiConsentData);
        transactionRequest.setBank(bank);

        return transactionRequest;
    }

    /**
     * Mapper to convert single payment body from context to multibanking request dto.
     */
    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface PaymentStatusMapper {
        PaymentStatusReqest map(PaymentInitiateBody contextPayment);
    }
}
