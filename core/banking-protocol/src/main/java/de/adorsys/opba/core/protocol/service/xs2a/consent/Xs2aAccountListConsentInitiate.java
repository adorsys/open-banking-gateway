package de.adorsys.opba.core.protocol.service.xs2a.consent;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.ConsentCreationResponse;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service("xs2aAccountListConsentInitiate")
@RequiredArgsConstructor
public class Xs2aAccountListConsentInitiate implements JavaDelegate {

    private final AccountInformationService ais;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        Xs2aContext context = delegateExecution.getVariable(CONTEXT, Xs2aContext.class);

        Response<ConsentCreationResponse> consentInit = ais.createConsent(
                context.toHeaders(),
                consents()
        );

        context.setRedirectUriOk("http://localhost:8080/v1/consents/confirm/accounts/" + delegateExecution.getProcessInstanceId() + "/");
        context.setConsentId(consentInit.getBody().getConsentId());
        delegateExecution.setVariable(CONTEXT, context);
    }

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
