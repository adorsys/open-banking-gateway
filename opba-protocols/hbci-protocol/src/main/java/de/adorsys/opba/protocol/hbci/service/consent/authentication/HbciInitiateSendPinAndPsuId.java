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
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;
import org.kapott.hbci.manager.HBCIProduct;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service("hbciInitiateSendPinAndPsuId")
@RequiredArgsConstructor
@Slf4j
public class HbciInitiateSendPinAndPsuId extends ValidatedExecution<HbciContext> {

    private final Optional<HBCIProduct> product;
    private final OnlineBankingService onlineBankingService;
    private final HbciAuthorizationPossibleErrorHandler errorSink;


    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {

        errorSink.handlePossibleAuthorizationError(
                () -> askForCredentials(execution, context),
                ex -> aisOnWrongCredentials(execution)
        );
    }

    private void askForCredentials(DelegateExecution execution, HbciContext context) {
        context.setWrongAuthCredentials(false);
        HbciConsent consent = context.getHbciDialogConsent();
        if (null == consent) {
            consent = new HbciConsent();
            consent.setHbciProduct(product.orElse(null));
            consent.setCredentials(Credentials.builder()
                    .userId(context.getPsuId())
                    .pin(context.getPsuPin())
                    .build()
            );
        } else if (Strings.isNotBlank(context.getPsuPin())) {
            // force to use new entered pin
            consent.getCredentials().setPin(context.getPsuPin());
        }

        UpdatePsuAuthenticationRequest request = new UpdatePsuAuthenticationRequest();
        request.setCredentials(consent.getCredentials());
        request.setBankApiConsentData(consent);
        request.setBank(context.getBank());

        UpdateAuthResponse response = onlineBankingService.getStrongCustomerAuthorisation().updatePsuAuthentication(request);

        if (handleScaChallengeRequired(execution, response)) {
            return;
        }

        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> ctx.setHbciDialogConsent((HbciConsent) response.getBankApiConsentData())
        );
    }

    private void aisOnWrongCredentials(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (HbciContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect credentials in HbciInitiateSendPinAndPsuID", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
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
