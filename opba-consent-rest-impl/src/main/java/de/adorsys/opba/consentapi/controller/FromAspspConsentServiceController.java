package de.adorsys.opba.consentapi.controller;

import de.adorsys.opba.consentapi.resource.generated.FromAspspConsentAuthorizationApi;
import de.adorsys.opba.consentapi.service.FromAspspMapper;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.facade.services.authorization.FromAspspRedirectHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class FromAspspConsentServiceController implements FromAspspConsentAuthorizationApi {

    private final FromAspspMapper aspspMapper;
    private final FromAspspRedirectHandler fromAspspRedirectHandler;
    private final FacadeServiceableRequest serviceableTemplate;

    @Override
    public CompletableFuture fromAspspOkUsingGET(
            String authId,
            String fromAspspRedirectCode,
            String code) {

        return fromAspspRedirectHandler.execute(
                FromAspspRequest.builder()
                        .facadeServiceable(serviceableTemplate.toBuilder()
                                .redirectCode(fromAspspRedirectCode)
                                .authorizationSessionId(authId)
                                .build()
                        )
                        .isOk(true)
                        .code(code)
                        .build()
        ).thenApply(aspspMapper::translate);
    }

    @Override
    public CompletableFuture fromAspspNokUsingGET(
            String authId,
            String fromAspspRedirectCode) {

        return fromAspspRedirectHandler.execute(
                FromAspspRequest.builder()
                        .facadeServiceable(serviceableTemplate.toBuilder()
                                .redirectCode(fromAspspRedirectCode)
                                .authorizationSessionId(authId)
                                .build()
                        )
                        .isOk(false)
                        .build()
        ).thenApply(aspspMapper::translate);
    }
}
