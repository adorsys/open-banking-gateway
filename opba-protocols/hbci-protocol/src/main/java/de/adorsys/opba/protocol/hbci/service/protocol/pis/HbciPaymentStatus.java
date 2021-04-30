package de.adorsys.opba.protocol.hbci.service.protocol.pis;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
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
import de.adorsys.opba.protocol.hbci.service.protocol.HbciUtil;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciPaymentStatusExecutor")
@RequiredArgsConstructor
public class HbciPaymentStatus extends ValidatedExecution<PaymentHbciContext> {

    private final OnlineBankingService onlineBankingService;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, PaymentHbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
        HbciConsent consent = context.getHbciDialogConsent();

        PaymentStatusReqest paymentStatusReqest = new PaymentStatusReqest();
        paymentStatusReqest.setPaymentId(context.getPayment().getPaymentId());
        paymentStatusReqest.setPsuAccount(HbciUtil.buildBankAccount(context.getAccountIban()));

        TransactionRequest<PaymentStatusReqest> request = create(paymentStatusReqest, new BankApiUser(),
                new BankAccess(), context.getBank(), consent);
        logResolver.log("getPaymentStatus request: {}", request);
        PaymentStatusResponse response = onlineBankingService.getStrongCustomerAuthorisation().getPaymentStatus(request);
        logResolver.log("getPaymentStatus response: {}", response);

        boolean postScaRequired = HbciScaRequiredUtil.extraCheckIfScaRequired(response);
        logResolver.log("AuthorisationCodeResponse is empty: {}, postScaRequired: {}", response.getAuthorisationCodeResponse() == null, postScaRequired);
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
}
