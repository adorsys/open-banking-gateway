package de.adorsys.opba.protocol.hbci.service.protocol.pis;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.PaymentResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.AbstractPayment;
import de.adorsys.multibanking.domain.transaction.SinglePayment;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.service.consent.HbciScaRequiredUtil;
import de.adorsys.opba.protocol.hbci.service.protocol.HbciUtil;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PisSinglePaymentResult;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

@Service("hbciPaymentExecutor")
@RequiredArgsConstructor
public class HbciPayment extends ValidatedExecution<PaymentHbciContext> {

    private final OnlineBankingService onlineBankingService;
    private final PaymentMapper paymentMapper;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, PaymentHbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        HbciConsent consent = context.getHbciDialogConsent();
        SinglePayment singlePayment = paymentMapper.map(context.getPayment());
        singlePayment.setPsuAccount(HbciUtil.buildBankAccount(context.getAccountIban()));

        TransactionRequest<SinglePayment> request = create(singlePayment, new BankApiUser(), new BankAccess(),
                context.getBank(), consent);
        PaymentResponse response = onlineBankingService.executePayment(request);
        boolean postScaRequired = HbciScaRequiredUtil.extraCheckIfScaRequired(response);

        logResolver.log("AuthorisationCodeResponse is empty: {}, postScaRequired: {}", response.getAuthorisationCodeResponse() == null, postScaRequired);
        if (null == response.getAuthorisationCodeResponse() && !postScaRequired) {
            ContextUtil.getAndUpdateContext(
                    execution,
                    (PaymentHbciContext ctx) -> {
                        ctx.setResponse(new PisSinglePaymentResult(response.getTransactionId()));
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
                    ctx.setChallengeData(response.getAuthorisationCodeResponse().getUpdateAuthResponse().getChallenge());
                }
        );
    }

    public <T extends AbstractPayment> TransactionRequest<T> create(T transaction,
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
    public interface PaymentMapper {
        @Mapping(source = "creditorName", target = "receiver")
        @Mapping(source = "creditorAccount.iban", target = "receiverIban")
        @Mapping(source = "remittanceInformationUnstructured", target = "purpose")
        @Mapping(expression = "java(new java.math.BigDecimal(contextPayment.getInstructedAmount().getAmount()))", target = "amount")
        @Mapping(expression = "java(org.iban4j.Iban.valueOf(accountReference.getIban()).getBankCode())", target = "psuAccount.blz")
        @Mapping(expression = "java(org.iban4j.Iban.valueOf(accountReference.getIban()).getBranchCode())", target = "psuAccount.bic")
        @Mapping(source = "debtorAccount.currency", target = "psuAccount.currency")
        @Mapping(source = "debtorAccount.iban", target = "psuAccount.iban")
        SinglePayment map(PaymentInitiateBody contextPayment);
    }
}
