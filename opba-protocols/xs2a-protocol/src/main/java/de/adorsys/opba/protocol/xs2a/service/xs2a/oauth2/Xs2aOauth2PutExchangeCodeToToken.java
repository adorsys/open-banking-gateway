package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.QueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Headers;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2WithCodeParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.Oauth2Service;
import de.adorsys.xs2a.adapter.service.model.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aOauth2ExchangeCodeToToken")
@RequiredArgsConstructor
public class Xs2aOauth2PutExchangeCodeToToken extends ValidatedExecution<Xs2aContext> {

    private final Xs2aValidator validator;
    private final Extractor extractor;
    private final Oauth2Service oauth2Service;

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ValidatedQueryHeaders<Xs2aOauth2WithCodeParameters, Xs2aOauth2Headers> validated = extractor.forExecution(context);
        TokenResponse response = oauth2Service.getToken(
                validated.getHeaders().toHeaders().toMap(),
                validated.getQuery().toParameters()
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> ctx.setOauth2token(response)
        );
    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Service
    public static class Extractor extends QueryHeadersMapperTemplate<Xs2aContext, Xs2aOauth2WithCodeParameters, Xs2aOauth2Headers> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aOauth2Headers> toHeaders,
                DtoMapper<Xs2aContext, Xs2aOauth2WithCodeParameters> toQuery) {
            super(toHeaders, toQuery);
        }
    }
}
