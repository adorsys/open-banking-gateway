package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.domain.request.UpdatePsuAuthenticationRequest;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.kapott.hbci.manager.HBCIProduct;
import org.springframework.stereotype.Service;

@Service("hbciInitiateSendPinAndPsuId")
@RequiredArgsConstructor
public class HbciInitiateSendPinAndPsuId extends ValidatedExecution<HbciContext> {

    private final HBCIProduct product;
    private final OnlineBankingService onlineBankingService;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        HbciConsent consent = new HbciConsent();
        consent.setHbciProduct(product);
        consent.setCredentials(Credentials.builder()
                .userId(context.getPsuId())
                .pin(context.getPsuPin())
                .build()
        );

        UpdatePsuAuthenticationRequest request = new UpdatePsuAuthenticationRequest();
        request.setCredentials(consent.getCredentials());
        request.setBankApiConsentData(consent);
        request.setBank(context.getBank());

        UpdateAuthResponse response =
                onlineBankingService.getStrongCustomerAuthorisation().updatePsuAuthentication(request);

        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData())
        );
    }
}
