package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.multibanking.domain.BankAccess;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionAuthorisationRequest;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.multibanking.hbci.model.HbciTanSubmit;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;


/**
 * Sends TAN challenge acquired from user.
 */
@Service("hbciSendTanChallenge")
@RequiredArgsConstructor
public class HbciSendTanChallenge extends ValidatedExecution<HbciContext> {

    private final OnlineBankingService onlineBankingService;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        HbciConsent consent = context.getHbciDialogConsent();

        TransactionAuthorisationRequest request = create(new BankApiUser(), new BankAccess(), context.getBank(), consent);
        request.setScaAuthenticationData(context.getPsuTan());

        UpdateAuthResponse response = onlineBankingService.getStrongCustomerAuthorisation().authorizeConsent(request);

        HbciConsent hbciConsent = (HbciConsent)response.getBankApiConsentData();
        HbciTanSubmit hbciTanSubmit = (HbciTanSubmit)hbciConsent.getHbciTanSubmit();

        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                    ctx.setHbciPassportState(hbciTanSubmit.getPassportState());
                }
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
}
