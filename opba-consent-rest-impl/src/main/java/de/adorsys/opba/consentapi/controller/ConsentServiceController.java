package de.adorsys.opba.consentapi.controller;

import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import de.adorsys.opba.consentapi.resource.generated.ConsentAuthorizationApi;
import de.adorsys.opba.consentapi.service.mapper.AisConsentMapper;
import de.adorsys.opba.consentapi.service.mapper.AisExtrasMapper;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.authorization.UpdateAuthorizationService;
import de.adorsys.opba.protocol.facade.services.fromaspsp.FromAspspRedirectHandler;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ConsentServiceController implements ConsentAuthorizationApi {

    private final AisExtrasMapper extrasMapper;
    private final AisConsentMapper aisConsentMapper;
    private final FacadeResponseMapper mapper;
    private final UpdateAuthorizationService updateAuthorizationService;
    private final FromAspspRedirectHandler fromAspspRedirectHandler;

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
                                .redirectCode(redirectCode)
                                .authorizationSessionId(authId)
                                .requestId(xRequestID)
                                .build()
                        )
                        .aisConsent(aisConsentMapper.map(body))
                        .scaAuthenticationData(body.getScaAuthenticationData())
                        .extras(extrasMapper.map(body.getExtras()))
                        .build()
        ).thenApply((FacadeResult<UpdateAuthBody> result) -> mapper.translate(result, null));
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
        ).thenApply((FacadeResult<UpdateAuthBody> result) -> mapper.translate(result, null));
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
        ).thenApply((FacadeResult<UpdateAuthBody> result) -> mapper.translate(result, null));
    }
}
