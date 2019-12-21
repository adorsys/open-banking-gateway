package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.service.ValidatedExternalServiceCall;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aAccountListConsentInitiate")
@RequiredArgsConstructor
public class Xs2aAccountListConsentInitiate extends ValidatedExternalServiceCall<Xs2aContext> {

    private final AccountInformationService ais;

    @Override
    protected void doCallRealService(DelegateExecution execution, Xs2aContext context) {
        Response<ConsentCreationResponse> consentInit = ais.createConsent(
                context.toHeaders(),
                consents()
        );
        context.setRedirectUriOk("http://localhost:8080/v1/consents/confirm/accounts/" + execution.getProcessInstanceId() + "/");
        context.setConsentId(consentInit.getBody().getConsentId());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doCallMockService(DelegateExecution execution, Xs2aContext context) {
    }

    @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
    private Consents consents() {
        Consents consents = new Consents();
        AccountAccess access = new AccountAccess();
        access.setAvailableAccounts(AccountAccess.AvailableAccountsEnum.ALLACCOUNTS);
        consents.setAccess(access);
        consents.setCombinedServiceIndicator(false);
        consents.setRecurringIndicator(true);
        consents.setFrequencyPerDay(10);
        consents.setValidUntil(LocalDate.of(2021, 10, 10));

        return consents;
    }
}
