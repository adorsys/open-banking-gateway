package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded.AuthorizationPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvidePsuIdAndPsuPasswordBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Headers;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.EmbeddedPreAuthorisationService;
import de.adorsys.xs2a.adapter.api.model.EmbeddedPreAuthorisationRequest;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import de.adorsys.opba.protocol.xs2a.service.mapper.HeadersBodyMapperTemplate;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;

@Slf4j
@Service("xs2aEmbeddedPreAuthorization")
@RequiredArgsConstructor
public class Xs2aEmbeddedPreAuthorization extends ValidatedExecution<Xs2aContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AuthorizationPossibleErrorHandler errorSink;
    private final EmbeddedPreAuthorisationService embeddedPreAuthorisationService;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    @SneakyThrows
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ValidatedHeadersBody<Xs2aOauth2Headers, EmbeddedPreAuthorisationRequest> validated = extractor.forExecution(context);
        errorSink.handlePossibleAuthorizationError(
                () -> getOauthEmbeddedTokenWithPassword(execution, context.aspspProfile(), validated),
                ex -> onWrongPassword(execution)
        );

    }

    private void onWrongPassword(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect password", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
        );
    }

    private void getOauthEmbeddedTokenWithPassword(DelegateExecution execution, CurrentBankProfile config, ValidatedHeadersBody<Xs2aOauth2Headers, EmbeddedPreAuthorisationRequest> validated) {

        TokenResponse response = this.embeddedPreAuthorisationService.getToken(validated.getBody(), validated.getHeaders().toHeaders());
        if (response.getTokenType() == null) {
            response.setTokenType(GlobalConst.BEARER_TOKEN_TYPE);
        }
        logResolver.log("getToken response: {}", response);
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setWrongAuthCredentials(false);
                    ctx.setOauth2Token(response);
                    ctx.setEmbeddedPreAuthNeeded(false);
                    ctx.setEmbeddedPreAuthDone(true);
                    ctx.setAspspScaApproach(null != config.getPreferredApproach() ? config.getPreferredApproach().name() : Approach.EMBEDDED.name());
                    log.info("aspsp sca approach: {}", ctx.getAspspScaApproach());
                }
        );

    }

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Service
    public static class Extractor extends HeadersBodyMapperTemplate<Xs2aContext, Xs2aOauth2Headers, ProvidePsuIdAndPsuPasswordBody, EmbeddedPreAuthorisationRequest> {

        public Extractor(
                DtoMapper<ProvidePsuIdAndPsuPasswordBody, EmbeddedPreAuthorisationRequest> toBody,
                DtoMapper<Xs2aContext, Xs2aOauth2Headers> toHeaders,
                DtoMapper<Xs2aContext, ProvidePsuIdAndPsuPasswordBody> toValidatableBody) {
            super(toValidatableBody, toBody, toHeaders);

        }
    }


    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<ProvidePsuIdAndPsuPasswordBody, EmbeddedPreAuthorisationRequest> {
        @Mapping(target = "password", source = "psuPassword")
        @Mapping(target = "username", source = "psuId")
        EmbeddedPreAuthorisationRequest map(ProvidePsuIdAndPsuPasswordBody validatableBody);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = GlobalConst.XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, ProvidePsuIdAndPsuPasswordBody> {
        ProvidePsuIdAndPsuPasswordBody map(Xs2aContext ctx);
    }


}
