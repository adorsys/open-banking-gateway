package de.adorsys.opba.consentapi.controller;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.consentapi.Const;
import de.adorsys.opba.consentapi.model.generated.AisAccountAccessInfo;
import de.adorsys.opba.consentapi.model.generated.AisConsentRequest;
import de.adorsys.opba.consentapi.model.generated.ConsentAuth;
import de.adorsys.opba.consentapi.model.generated.PaymentProduct;
import de.adorsys.opba.consentapi.model.generated.ScaUserData;
import de.adorsys.opba.consentapi.model.generated.SinglePayment;
import de.adorsys.opba.consentapi.resource.generated.AuthStateConsentAuthorizationApi;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.authorization.GetAuthorizationStateService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.RedirectionOnlyToOkMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@RestController
@RequiredArgsConstructor
public class AuthStateConsentServiceController implements AuthStateConsentAuthorizationApi {

    private static final Map<String, String> TRANSLATE_ACTIONS = ImmutableMap
            .of("SINGLE_PAYMENT", "INITIATE_PAYMENT");
    private final FacadeServiceableRequest serviceableTemplate;
    private final UserAgentContext userAgentContext;
    private final GetAuthorizationStateService authorizationStateService;
    private final RedirectionOnlyToOkMapper redirectionOnlyToOkMapper;
    private final AuthStateBodyToApiMapper authStateMapper;

    @Override
    public CompletableFuture authUsingGET(
            String authId,
            String redirectCode) {

        return authorizationStateService.execute(
                AuthorizationRequest.builder()
                        .facadeServiceable(serviceableTemplate.toBuilder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .redirectCode(redirectCode)
                                .authorizationSessionId(authId)
                                .build()
                        )
                        .build()
        ).thenApply((FacadeResult<AuthStateBody> result) -> redirectionOnlyToOkMapper.translate(result, authStateMapper));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface AuthStateBodyToApiMapper extends FacadeResponseBodyToRestBodyMapper<ConsentAuth, AuthStateBody> {

        @Mapping(source = "requestData.singlePaymentBody", target = "singlePayment")
        @Mapping(source = "requestData.aisConsent", target = "consent")
        @Mapping(source = "requestData.bankName", target = "bankName")
        @Mapping(source = "requestData.fintechName", target = "fintechName")
        ConsentAuth map(AuthStateBody authStateBody);

        @Mapping(source = "key", target = "id")
        @Mapping(source = "value", target = "methodValue")
        ScaUserData fromScaMethod(ScaMethod method);

        default ConsentAuth.ActionEnum fromString(String value) {
            return ConsentAuth.ActionEnum.fromValue(TRANSLATE_ACTIONS.getOrDefault(value, value));
        }

        @Mapping(source = "singlePaymentBody.creditorAddress.postCode", target = "creditorAddress.postalCode")
        @Mapping(source = "singlePaymentBody.creditorAddress.streetName", target = "creditorAddress.street")
        SinglePayment mapToSinglePayment(SinglePaymentBody singlePaymentBody);

        AisConsentRequest mapToAisConsentRequest(AisConsent aisConsent);

        default PaymentProduct mapToProduct(PaymentProductDetails value) {
            if (null == value) {
                return null;
            }
            return PaymentProduct.fromValue(value.name());
        }

        default AisAccountAccessInfo.AllPsd2Enum mapToAllPsd2Enum(String value) {
            if (null == value) {
                return null;
            }
            return AisAccountAccessInfo.AllPsd2Enum.fromValue(value);
        }

        default AisAccountAccessInfo.AvailableAccountsEnum mapToAvailableAccountsEnum(String value) {
            if (null == value) {
                return null;
            }
            return AisAccountAccessInfo.AvailableAccountsEnum.fromValue(value);
        }
    }
}
