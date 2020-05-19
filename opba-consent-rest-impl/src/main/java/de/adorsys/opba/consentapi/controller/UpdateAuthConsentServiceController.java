package de.adorsys.opba.consentapi.controller;

import de.adorsys.opba.consentapi.model.generated.DenyRequest;
import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import de.adorsys.opba.consentapi.resource.generated.ConsentAuthorizationApi;
import de.adorsys.opba.consentapi.service.mapper.AisConsentMapper;
import de.adorsys.opba.consentapi.service.mapper.AisExtrasMapper;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.DenyAuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DenyAuthBody;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.authorization.DenyAuthorizationService;
import de.adorsys.opba.protocol.facade.services.authorization.UpdateAuthorizationService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class UpdateAuthConsentServiceController implements ConsentAuthorizationApi {

    private final FacadeServiceableRequest serviceableTemplate;
    private final UserAgentContext userAgentContext;
    private final UpdateAuthorizationService updateAuthorizationService;
    private final DenyAuthorizationService denyAuthorizationService;
    private final AisExtrasMapper extrasMapper;
    private final AisConsentMapper aisConsentMapper;
    private final FacadeResponseMapper mapper;

    @Override
    public CompletableFuture embeddedUsingPOST(
            UUID xRequestID,
            String xXsrfToken,
            String authId,
            PsuAuthRequest body,
            String redirectCode) {
        return updateAuthorizationService.execute(
                AuthorizationRequest.builder()
                        .facadeServiceable(serviceableTemplate.toBuilder()
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
            String authId) {

        return denyAuthorizationService.execute(DenyAuthorizationRequest.builder()
                .facadeServiceable(serviceableTemplate.toBuilder()
                        // Get rid of CGILIB here by copying:
                        .uaContext(userAgentContext.toBuilder().build())
                        .authorizationSessionId(authId)
                        .requestId(xRequestID)
                        .build()
                )
                .build()
        ).thenApply((FacadeResult<DenyAuthBody> result) -> mapper.translate(result, new NoOpMapper<>()));
    }

    public static class NoOpMapper<T> implements FacadeResponseBodyToRestBodyMapper<T, T> {
        public T map(T facadeEntity) {
            return facadeEntity;
        }
    }
}
