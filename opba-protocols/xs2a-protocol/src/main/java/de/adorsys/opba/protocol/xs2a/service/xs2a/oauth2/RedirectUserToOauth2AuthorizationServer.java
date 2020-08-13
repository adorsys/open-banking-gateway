package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.RedirectToAspsp;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.QueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Headers;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Parameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.Oauth2Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * We expect PKCE authorization from Xs2a adapter (no state, etc.)
 */
@Service("xs2aRedirectUserToOauth2AuthorizationServer")
@RequiredArgsConstructor
public class RedirectUserToOauth2AuthorizationServer extends ValidatedExecution<Xs2aContext> {

    private final ProtocolUrlsConfiguration urlsConfiguration;
    private final Xs2aValidator validator;
    private final Extractor extractor;
    private final Oauth2Service oauth2Service;
    private final Xs2aRedirectExecutor redirectExecutor;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ProtocolUrlsConfiguration.UrlSet urlSet = urlsConfiguration.getUrlAisOrPisSetBasedOnContext(context);

        ValidatedQueryHeaders<Xs2aOauth2Parameters, Xs2aOauth2Headers> validated = extractor.forExecution(context);

        URI oauth2RedirectUserTo = oauth2Service.getAuthorizationRequestUri(
                validated.getHeaders().toHeaders().toMap(),
                validated.getQuery().toParameters()
        );

        redirectExecutor.redirect(
                execution,
                context,
                urlSet.getToAspsp(),
                oauth2RedirectUserTo.toASCIIString(),
                redirect -> new RedirectToAspsp(redirect.build())
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Service
    public static class Extractor extends QueryHeadersMapperTemplate<Xs2aContext, Xs2aOauth2Parameters, Xs2aOauth2Headers> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aOauth2Headers> toHeaders,
                DtoMapper<Xs2aContext, Xs2aOauth2Parameters> toQuery) {
            super(toHeaders, toQuery);
        }
    }
}
