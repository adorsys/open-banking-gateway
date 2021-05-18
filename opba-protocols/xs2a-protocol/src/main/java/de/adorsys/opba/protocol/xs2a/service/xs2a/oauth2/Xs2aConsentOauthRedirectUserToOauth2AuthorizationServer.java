package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.RedirectToAspsp;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.ing.IngOauth2Api;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Special case step to handle 'consentOauth' case from the Xs2a adapter.
 * We expect PKCE authorization from Xs2a adapter (no state, etc.)
 */
@Service("xs2aConsentOauthRedirectUserToOauth2AuthorizationServer")
@RequiredArgsConstructor
public class Xs2aConsentOauthRedirectUserToOauth2AuthorizationServer extends ValidatedExecution<Xs2aContext> {

    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final RuntimeService runtimeService;
    private final IngOauth2Api ingOauth2Api;
    private final Xs2aRedirectExecutor redirectExecutor;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        String scope = "";
        ProtocolUrlsConfiguration.UrlSet urlSet = urlsConfiguration.getUrlAisOrPisSetBasedOnContext(context);
        String redirectBack = ContextUtil.buildAndExpandQueryParameters(urlSet.getWebHooks().getOk(), context).toASCIIString();
        logResolver.log(String.format("getAuthorizationUrl with parameters: %s,%s", scope, redirectBack), execution);

        String oauth2RedirectUserTo = ingOauth2Api.getAuthorizationUrl(Collections.emptyList(), scope, redirectBack).getBody().getLocation();

        logResolver.log("getAuthorizationUrl response: {}", oauth2RedirectUserTo);

        redirectExecutor.redirect(
                execution,
                context,
                urlsConfiguration.getUrlAisOrPisSetBasedOnContext(context).getToAspsp(),
                oauth2RedirectUserTo,
                redirect -> new RedirectToAspsp(redirect.build())
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        // NOP
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        runtimeService.trigger(execution.getId());
    }
}
