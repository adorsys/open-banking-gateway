package de.adorsys.opba.consentapi.controller;

import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import de.adorsys.opba.consentapi.resource.generated.ConsentAuthorizationApi;
import de.adorsys.opba.consentapi.service.mapper.AisConsentMapper;
import de.adorsys.opba.consentapi.service.mapper.AisExtrasMapper;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.authorization.FromAspspRedirectHandler;
import de.adorsys.opba.protocol.facade.services.authorization.GetAuthorizationStateService;
import de.adorsys.opba.protocol.facade.services.authorization.UpdateAuthorizationService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.restapi.shared.service.RedirectionOnlyToOkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ConsentServiceController implements ConsentAuthorizationApi {

    private final UserAgentContext userAgentContext;
    private final AisExtrasMapper extrasMapper;
    private final AisConsentMapper aisConsentMapper;
    private final RedirectionOnlyToOkMapper redirectionOnlyToOkMapper;
    private final FacadeResponseMapper mapper;
    private final GetAuthorizationStateService authorizationStateService;
    private final UpdateAuthorizationService updateAuthorizationService;
    private final FromAspspRedirectHandler fromAspspRedirectHandler;

    @Override
    public CompletableFuture authUsingGET(String authId, String redirectCode) {
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
        ).thenApply(redirectionOnlyToOkMapper::translate);
    }

    @Override
    public CompletableFuture embeddedUsingPOST(
            UUID xRequestID,
            String xXsrfToken,
            String authId,
            PsuAuthRequest body,
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
    public CompletableFuture fromAspspOkUsingGET(
        String authId,
        String redirectState,
        String redirectCode) {

        return fromAspspRedirectHandler.execute(
            FromAspspRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                    .redirectCode(redirectCode)
                    .authorizationSessionId(authId)
                    .build()
                )
                .isOk(true)
                .build()
        ).thenApply((FacadeResult<UpdateAuthBody> result) ->
                mapper.translate(result, new NoOpMapper<>()));
    }

    @Override
    public CompletableFuture fromAspspNokUsingGET(
        String authId,
        String redirectState,
        String redirectCode) {

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
}
