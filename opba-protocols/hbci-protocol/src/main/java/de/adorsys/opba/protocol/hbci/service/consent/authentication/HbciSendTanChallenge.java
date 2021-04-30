package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionAuthorisationRequest;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;


/**
 * Sends TAN challenge acquired from user.
 */
@Service("hbciSendTanChallenge")
@RequiredArgsConstructor
@Slf4j
public class HbciSendTanChallenge extends ValidatedExecution<HbciContext> {

    private final OnlineBankingService onlineBankingService;
    private final HbciAuthorizationPossibleErrorHandler errorSink;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        errorSink.handlePossibleAuthorizationError(
                () -> askForCredentials(execution, context),
                ex -> aisOnWrongCredentials(execution)
        );
    }

    public static TransactionAuthorisationRequest create(
            BankApiUser bankApiUser,
            BankAccess bankAccess,
            Bank bank,
            Object bankApiConsentData) {
        TransactionAuthorisationRequest transactionAuthorisationRequest = new TransactionAuthorisationRequest();
        transactionAuthorisationRequest.setBankApiUser(bankApiUser);
        transactionAuthorisationRequest.setBankAccess(bankAccess);
        transactionAuthorisationRequest.setBankApiConsentData(bankApiConsentData);
        transactionAuthorisationRequest.setBank(bank);

        return transactionAuthorisationRequest;
    }

    private void askForCredentials(DelegateExecution execution, HbciContext context) {
        context.setWrongAuthCredentials(false);
        HbciConsent consent = context.getHbciDialogConsent();

        TransactionAuthorisationRequest request = create(new BankApiUser(), new BankAccess(), context.getBank(), consent);
        request.setScaAuthenticationData(context.getPsuTan());

        logResolver.log("authorizeConsent request: {}", request);

        UpdateAuthResponse response = onlineBankingService.getStrongCustomerAuthorisation().authorizeConsent(request);

        logResolver.log("authorizeConsent response: {}", response);

        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    ctx.setWrongAuthCredentials(false);
                    ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                }
        );
    }

    private void aisOnWrongCredentials(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect credentials", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
        );
    }
}
