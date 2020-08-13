package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.TanTransportType;
import de.adorsys.multibanking.domain.request.SelectPsuAuthenticationMethodRequest;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.HbciRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks PSU for his SCA challenge result by redirect him to password input page. Suspends process to wait for users' input.
 */
@Service("hbciSubmitSelectedMethodAndAskForTanChallenge")
@RequiredArgsConstructor
public class HbciSubmitSelectedMethodAndAskForTanChallenge extends ValidatedExecution<HbciContext> {

    private final OnlineBankingService onlineBankingService;
    private final RuntimeService runtimeService;
    private final HbciRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        HbciConsent consent = context.getHbciDialogConsent();
        String selectedTanId = context.getUserSelectScaId();

        SelectPsuAuthenticationMethodRequest request = create(new BankApiUser(), new BankAccess(), context.getBank(), consent);
        TanTransportType selected = consent.getTanMethodList()
                .stream().filter(it -> it.getId().equals(selectedTanId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown TAN method ID: " + selectedTanId));

        request.setAuthenticationMethodId(selected.getId());

        UpdateAuthResponse response = onlineBankingService.getStrongCustomerAuthorisation().selectPsuAuthenticationMethod(request);
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData())
        );

        redirectExecutor.redirect(execution, context, redir -> redir.getParameters().getReportScaResult());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, HbciContext context) {
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> ctx.setPsuTan("mock-challenge")
        );

        runtimeService.trigger(execution.getId());
    }

    public static SelectPsuAuthenticationMethodRequest create(
            BankApiUser bankApiUser,
            BankAccess bankAccess,
            Bank bank,
            Object bankApiConsentData) {
        SelectPsuAuthenticationMethodRequest selectMethodRequest = new SelectPsuAuthenticationMethodRequest();
        selectMethodRequest.setBankApiUser(bankApiUser);
        selectMethodRequest.setBankAccess(bankAccess);
        selectMethodRequest.setBankApiConsentData(bankApiConsentData);
        selectMethodRequest.setBank(bank);

        return selectMethodRequest;
    }
}
