package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.domain.ScaStatus;
import de.adorsys.multibanking.domain.request.UpdatePsuAuthenticationRequest;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.kapott.hbci.manager.HBCIProduct;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service("hbciInitiateSendPinAndPsuId")
@RequiredArgsConstructor
public class HbciInitiateSendPinAndPsuId extends ValidatedExecution<HbciContext> {

    private final Optional<HBCIProduct> product;
    private final OnlineBankingService onlineBankingService;

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        HbciConsent consent = context.getHbciDialogConsent();
        if (null == consent) {
            consent = new HbciConsent();
            consent.setHbciProduct(product.orElse(null));
            consent.setCredentials(Credentials.builder()
                    .userId(context.getPsuId())
                    .pin(context.getPsuPin())
                    .build()
            );
        }

        UpdatePsuAuthenticationRequest request = new UpdatePsuAuthenticationRequest();
        request.setCredentials(consent.getCredentials());
        request.setBankApiConsentData(consent);
        request.setBank(context.getBank());

        UpdateAuthResponse response =
                onlineBankingService.getStrongCustomerAuthorisation().updatePsuAuthentication(request);

        if (handleScaChallengeRequired(execution, response)) {
            return;
        }

        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData())
        );
    }

    private boolean handleScaChallengeRequired(DelegateExecution execution, UpdateAuthResponse response) {
        HbciConsent consent = (HbciConsent) response.getBankApiConsentData();
        if (ScaStatus.PSUAUTHENTICATED == response.getScaStatus() && consent.isWithHktan()) {
            ContextUtil.getAndUpdateContext(
                    execution,
                    (HbciContext ctx) -> {
                        ctx.setTanChallengeRequired(true);
                        ctx.setAvailableSca(
                                response.getScaMethods().stream()
                                        .map(it -> new ScaMethod(it.getId(), it.getName(), it.getName()))
                                        .collect(Collectors.toList())
                        );
                        ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData());
                    }
            );

            return true;
        }
        return false;
    }
}
