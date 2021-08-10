package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.config.HbciProtocolConfiguration;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.HbciRedirectExecutor;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Redirects PSU to the page that shows that consent was granted and has button to redirect PSU back to FinTech.
 */
@Service("hbciReportToFintechConsentAuthorized")
@RequiredArgsConstructor
public class ReportConsentAuthorizationFinished extends ValidatedExecution<HbciContext> {

    private final HbciRedirectExecutor redirectExecutor;
    private final HbciProtocolConfiguration configuration;
    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ContextUtil.getAndUpdateContext(execution, (HbciContext ctx) -> ctx.setConsentIncompatible(false));
        redirectExecutor.redirect(
            execution,
            context,
            ContextUtil.buildAndExpandQueryParameters(context.getActiveUrlSet(configuration).getRedirect().getWebHooks().getResult(), context).toASCIIString(),
            context.getFintechRedirectUriOk(),
            redirect -> new ConsentAcquired(redirect.build()));
    }
}
