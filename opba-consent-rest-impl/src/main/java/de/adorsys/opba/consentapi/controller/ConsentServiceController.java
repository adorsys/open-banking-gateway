package de.adorsys.opba.consentapi.controller;

import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import de.adorsys.opba.consentapi.resource.generated.ConsentAuthorizationApi;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authentication.AuthorizationRequest;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeErrorResult;
import de.adorsys.opba.protocol.facade.services.authorization.UpdateAuthorizationService;
import de.adorsys.opba.restapi.shared.service.ErrorResultMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.ais.model.generated.GeneralError;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ConsentServiceController implements ConsentAuthorizationApi {

    private final FacadeResponseMapper mapper;
    private final UpdateAuthorizationService updateAuthorizationService;
    private final ErrorResultMapper<FacadeErrorResult, GeneralError> errorMapper;

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
                                .xRequestID(xRequestID)
                                .build()
                        )
                        .scaAuthenticationData(body.getScaAuthenticationData())
                        .build()
        ).thenApply(res -> mapper.translate(res, errorMapper));
    }
}
