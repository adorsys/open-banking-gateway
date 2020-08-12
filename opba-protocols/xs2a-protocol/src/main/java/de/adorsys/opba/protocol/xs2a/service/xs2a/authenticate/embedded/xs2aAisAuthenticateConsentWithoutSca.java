package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Informs user about 0 SCA consent authentication result.
 */
@Service("xs2aInformsAboutZeroScaResult")
@RequiredArgsConstructor
public class xs2aAisAuthenticateConsentWithoutSca extends ValidatedExecution<Xs2aContext> {
    private final RuntimeService runtimeService;
    private final Xs2aRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, urls -> {
            ProtocolUrlsConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(context.getAction())
                                                              ? urls.getPis() : urls.getAis();
            return urlSet.getParameters().getReportScaResult();
        });
    }
}
