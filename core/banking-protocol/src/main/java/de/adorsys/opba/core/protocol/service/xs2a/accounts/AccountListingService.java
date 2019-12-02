package de.adorsys.opba.core.protocol.service.xs2a.accounts;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.service.xs2a.consent.ConsentConst;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.service.xs2a.accounts.AccountListConst.ACCOUNT_LIST;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.ACCEPT;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONSENT_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@Service("accountListing")
@RequiredArgsConstructor
public class AccountListingService implements JavaDelegate {

    private final AccountInformationService ais;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        String consentId = delegateExecution.getVariable(ConsentConst.CONSENT_ID, String.class);

        Response<AccountListHolder> accounts = ais.getAccountList(
                RequestHeaders.fromMap(
                        ImmutableMap.<String, String>builder()
                                .put(ACCEPT, "application/json")
                                .put(PSU_ID, "anton.brueckner")
                                .put(X_REQUEST_ID, "2f77a125-aa7a-45c0-b414-cea25a116035")
                                .put(CONTENT_TYPE, "application/json")
                                .put(PSU_IP_ADDRESS, "1.1.1.1")
                                .put(CONSENT_ID, consentId)
                                // Identifies bank for XS2A-adapter
                                .put(X_GTW_ASPSP_ID, "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46")
                                .build()
                ),
                RequestParams.fromMap(ImmutableMap.of("withBalance", "false"))
        );


        delegateExecution.setVariableLocal(ACCOUNT_LIST, accounts.getBody());
    }
}
