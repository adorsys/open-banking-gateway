package de.adorsys.opba.consentapi.controller;

import de.adorsys.opba.consentapi.Const;
import de.adorsys.opba.consentapi.model.generated.ConsentAuth;
import de.adorsys.opba.consentapi.model.generated.DenyRequest;
import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import de.adorsys.opba.consentapi.model.generated.ScaUserData;
import de.adorsys.opba.consentapi.resource.generated.ConsentAuthorizationApi;
import de.adorsys.opba.consentapi.service.FromAspspMapper;
import de.adorsys.opba.consentapi.service.mapper.AisConsentMapper;
import de.adorsys.opba.consentapi.service.mapper.AisExtrasMapper;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.DenyAuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.DenyAuthBody;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.authorization.DenyAuthorizationService;
import de.adorsys.opba.protocol.facade.services.authorization.FromAspspRedirectHandler;
import de.adorsys.opba.protocol.facade.services.authorization.GetAuthorizationStateService;
import de.adorsys.opba.protocol.facade.services.authorization.UpdateAuthorizationService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.restapi.shared.service.RedirectionOnlyToOkMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.consentapi.Const.SPRING_KEYWORD;

// TODO https://github.com/adorsys/open-banking-gateway/issues/457
//  Tear this controller to multiple smaller to reduce footprint
@RestController
@RequiredArgsConstructor
public class ConsentServiceController implements ConsentAuthorizationApi {

    private final UserAgentContext userAgentContext;
    private final AisExtrasMapper extrasMapper;
    private final AisConsentMapper aisConsentMapper;
    private final FromAspspMapper aspspMapper;
    private final AuthStateBodyToApiMapper authStateMapper;
    private final RedirectionOnlyToOkMapper redirectionOnlyToOkMapper;
    private final FacadeResponseMapper mapper;
    private final DenyAuthorizationService denyAuthorizationService;
    private final GetAuthorizationStateService authorizationStateService;
    private final UpdateAuthorizationService updateAuthorizationService;
    private final FromAspspRedirectHandler fromAspspRedirectHandler;

    @Override
    public CompletableFuture authUsingGET(
            String authId,
            String redirectCode,
            String xTimestampUTC,
            byte[] xRequestSignature,
            String fintechId) {

        return authorizationStateService.execute(
                AuthorizationRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .redirectCode(redirectCode)
                                .authorizationSessionId(authId)
                                .build()
                        )
                        .build()
        ).thenApply((FacadeResult<AuthStateBody> result) -> redirectionOnlyToOkMapper.translate(result, authStateMapper));
    }

    @Override
    public CompletableFuture embeddedUsingPOST(
            UUID xRequestID,
            String xXsrfToken,
            String authId,
            PsuAuthRequest body,
            String xTimestampUTC,
            byte[] xRequestSignature,
            String fintechId,
            String redirectCode) {

        return updateAuthorizationService.execute(
                AuthorizationRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .redirectCode(redirectCode)
                                .authorizationSessionId(authId)
                                .requestId(xRequestID)
                                .build()
                        )
                        .aisConsent(null == body.getConsentAuth() ? null : aisConsentMapper.map(body))
                        .scaAuthenticationData(body.getScaAuthenticationData())
                        .extras(extrasMapper.map(body.getExtras()))
                        .build()
        ).thenApply((FacadeResult<UpdateAuthBody> result) ->
                mapper.translate(result, new NoOpMapper<>()));
    }

    @Override
    public CompletableFuture denyUsingPOST(
            DenyRequest body,
            UUID xRequestID,
            String xXsrfToken,
            String authId,
            String xTimestampUTC,
            byte[] xRequestSignature,
            String fintechId) {

        return denyAuthorizationService.execute(DenyAuthorizationRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                        // Get rid of CGILIB here by copying:
                        .uaContext(userAgentContext.toBuilder().build())
                        .authorizationSessionId(authId)
                        .requestId(xRequestID)
                        .build()
                )
                .build()
        ).thenApply((FacadeResult<DenyAuthBody> result) -> mapper.translate(result, new NoOpMapper<>()));
    }

    @Override
    public CompletableFuture fromAspspOkUsingGET(
        String authId,
        String redirectState,
        String redirectCode,
        String xTimestampUTC,
        byte[] xRequestSignature,
        String fintechId) {

        return fromAspspRedirectHandler.execute(
            FromAspspRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                    .redirectCode(redirectCode)
                    .authorizationSessionId(authId)
                    .build()
                )
                .isOk(true)
                .build()
        ).thenApply(aspspMapper::translate);
    }

    @Override
    public CompletableFuture fromAspspNokUsingGET(
        String authId,
        String redirectState,
        String redirectCode,
        String xTimestampUTC,
        byte[] xRequestSignature,
        String fintechId) {

        return fromAspspRedirectHandler.execute(
            FromAspspRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                    .redirectCode(redirectCode)
                    .authorizationSessionId(authId)
                    .build()
                )
                .isOk(false)
                .build()
        ).thenApply((FacadeResult<UpdateAuthBody> result) ->
                mapper.translate(result, new NoOpMapper<>()));
    }

    public static class NoOpMapper<T> implements FacadeResponseBodyToRestBodyMapper<T, T> {
        public T map(T facadeEntity) {
            return facadeEntity;
        }
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface AuthStateBodyToApiMapper extends FacadeResponseBodyToRestBodyMapper<InlineResponse200, AuthStateBody> {

        @Mapping(source = "facade", target = "consentAuth")
        InlineResponse200 map(AuthStateBody facade);

        @Mapping(source = "key", target = "id")
        @Mapping(source = "value", target = "methodValue")
        ScaUserData fromScaMethod(ScaMethod method);

        default ConsentAuth.ActionEnum fromString(String value) {
            return ConsentAuth.ActionEnum.fromValue(value);
        }
    }
}
