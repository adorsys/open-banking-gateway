package de.adorsys.opba.tppbankingapi.service;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.consentapi.controller.AuthStateConsentServiceController;
import de.adorsys.opba.consentapi.controller.UpdateAuthConsentServiceController;
import de.adorsys.opba.consentapi.service.mapper.AisExtrasMapper;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.authorization.GetAuthorizationStateService;
import de.adorsys.opba.protocol.facade.services.authorization.PsuLoginService;
import de.adorsys.opba.protocol.facade.services.authorization.UpdateAuthorizationService;
import de.adorsys.opba.protocol.facade.services.pis.SinglePaymentService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.restapi.shared.service.RedirectionOnlyToOkMapper;
import de.adorsys.opba.tppbankingapi.controller.PasswordExtractingUtil;

import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.PaymentInitiation;
import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.SinglePayment;
import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.ConsentAuth;
import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.PaymentProduct;
import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.PaymentInitiationResponse;
import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.ScaUserData;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static de.adorsys.opba.tppbankingapi.Const.API_MAPPERS_PACKAGE;
import static de.adorsys.opba.tppbankingapi.Const.SPRING_KEYWORD;

@SuppressWarnings({"MethodLength", "ParameterNumber", "PMD.UnusedFormalParameter", "CPD-START"})
@RequiredArgsConstructor
@Service
@ComponentScan({
        "de.adorsys.opba.protocol.facade.services.pis",
        "de.adorsys.opba.protocol.facade.services.psu",
        "de.adorsys.opba.protocol.facade.services.authorization"
})
public class PaymentOrchestratedService {

    private final SinglePaymentService paymentService;
    private final PsuLoginService psuLoginService;
    private final GetAuthorizationStateService authorizationStateService;
    private final UpdateAuthorizationService updateAuthorizationService;
    private final UserAgentContext userAgentContext;
    private final AisConsentMapper aisConsentMapper;
    private final FacadeResponseMapper mapper;
    private final PaymentRestRequestBodyToSinglePaymentMapper paymentRestRequestBodyToSinglePaymentMapper;
    private final PaymentFacadeResponseBodyToRestBodyMapper paymentFacadeResponseBodyToRestBodyMapper;
    private final AisExtrasMapper extrasMapper;
    private final UpdateAuthConsentServiceController.UpdateAuthBodyToApiMapper updateAuthBodyToApiMapper;
    private final AuthStateConsentServiceController.AuthStateBodyToApiMapper authStateMapper;
    private final RedirectionOnlyToOkMapper redirectionOnlyToOkMapper;

    private static final Map<String, String> TRANSLATE_ACTIONS = ImmutableMap.of(
            "SINGLE_PAYMENT", "INITIATE_PAYMENT"
    );

    public CompletableFuture<ResponseEntity<?>> initiatePayment(
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String paymentProduct,
            PaymentInitiation body,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechID,
            String serviceSessionPassword,
            String fintechDataPassword,
            UUID bankProfileID,
            Boolean xPsuAuthenticationRequired,
            String xProtocolConfiguration,
            Boolean computePsuIpAddress,
            String psuIpAddress,
            Boolean fintechDecoupledPreferred,
            String fintechBrandLoggingInformation,
            String fintechNotificationURI,
            String fintechNotificationContentPreferred
    ) throws ExecutionException, InterruptedException {
        CompletableFuture<PaymentOrchestrationContext> context = triggerWholeAuthorization(fintechUserID,
                fintechRedirectURLOK,
                fintechRedirectURLNOK,
                xRequestID,
                paymentProduct,
                body,
                xTimestampUTC,
                xRequestSignature,
                fintechID,
                serviceSessionPassword,
                fintechDataPassword,
                bankProfileID,
                xPsuAuthenticationRequired,
                xProtocolConfiguration,
                computePsuIpAddress,
                psuIpAddress,
                fintechDecoupledPreferred,
                fintechBrandLoggingInformation,
                fintechNotificationURI,
                fintechNotificationContentPreferred);

        String newXsrfToken = (String) updateAuthorization(context).get();
        PaymentOrchestrationContext updatedContext = context.get().withRedirectCode(newXsrfToken);

        return refreshAuthorizationStateAfterUpdate(updatedContext);

    }

    /**
     * Handles the initial payment initiation response, extracts necessary IDs (authId, redirectCode, xsrfToken),
     * and performs the PSU login association to get the session key.
     *
     * @param paymentResponse The response from the initial payment service.
     * @param context         The orchestration context containing original request parameters.
     * @return A CompletableFuture that resolves to an updated PaymentOrchestrationContext
     * containing the extracted session details (authId, redirectCode, xsrfToken, sessionKey).
     */
    private CompletableFuture<PaymentOrchestrationContext> handlePaymentResponseAndPsuLogin(
            ResponseEntity<?> paymentResponse,
            PaymentOrchestrationContext context
    ) {
        // Extract required headers, throwing NRE if missing to indicate a critical issue.
        UUID authId = UUID.fromString(Objects.requireNonNull(paymentResponse.getHeaders().getFirst("Authorization-Session-ID"),
                "Authorization-Session-ID header missing from payment initiation response"));
        String redirectCode = extractRedirectCode(Objects.requireNonNull(paymentResponse.getHeaders().getFirst("Location"),
                "Location header missing from payment initiation response"));
        String xsrfToken = Objects.requireNonNull(paymentResponse.getHeaders().getFirst("X-XSRF-TOKEN"),
                "X-XSRF-TOKEN header missing from payment initiation response");

        // Perform PSU login to get the session key. This is an asynchronous call.
        return psuLoginService.anonymousPsuAssociateAuthSessionWithHeaders(authId, redirectCode)
                .thenApply(authHeaders -> {
                    // Extract the session key from the PSU login response.
                    String sessionKey = Objects.requireNonNull(authHeaders.getHeaders().get("Set-Cookie"),
                            "Set-Cookie header missing from PSU login response");
                    // Return a new context instance with all extracted and obtained auth details.
                    return context.withAuthDetails(authId, redirectCode, xsrfToken, sessionKey);
                });
    }

    /**
     * Triggers the authorization state service using details from the orchestration context.
     * This method prepares the AuthorizationRequest and executes the service.
     *
     * @param context The orchestration context containing all necessary details including authId, redirectCode, xsrfToken, and sessionKey.
     * @return A CompletableFuture that resolves to the result of the authorization state service.
     */
    private CompletableFuture<FacadeResult<AuthStateBody>> triggerAuthorizationState(
            PaymentOrchestrationContext context
    ) {
        // Build the AuthorizationRequest using data from the context.
        AuthorizationRequest authRequest = buildAuthRequest(
                context.getXsrfToken(),
                context.getSessionKey(),
                context.getFintechID(),
                context.getFintechUserID(),
                context.getXRequestID(),
                context.getBankProfileID(),
                context.getServiceSessionPassword(),
                context.getFintechDataPassword(),
                context.getFintechRedirectURLOK(),
                context.getFintechRedirectURLNOK(),
                context.getXPsuAuthenticationRequired(),
                context.getFintechDecoupledPreferred(),
                context.getFintechBrandLoggingInformation(),
                context.getFintechNotificationURI(),
                context.getFintechNotificationContentPreferred(),
                context.getUaContext(),
                context.getAuthId()
        );
        // Execute the authorization state service asynchronously.
        return authorizationStateService.execute(authRequest);
    }

    private CompletableFuture<PaymentOrchestrationContext> triggerWholeAuthorization(
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String paymentProduct,
            PaymentInitiation body,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechID,
            String serviceSessionPassword,
            String fintechDataPassword,
            UUID bankProfileID,
            Boolean xPsuAuthenticationRequired,
            String xProtocolConfiguration,
            Boolean computePsuIpAddress,
            String psuIpAddress,
            Boolean fintechDecoupledPreferred,
            String fintechBrandLoggingInformation,
            String fintechNotificationURI,
            String fintechNotificationContentPreferred
    ) {
        UserAgentContext context = userAgentContext.toBuilder().build();

        // Create an initial context object to pass relevant parameters through the chain.
        // Auth-related fields (authId, redirectCode, xsrfToken, sessionKey) are null initially.
        PaymentOrchestrationContext orchestrationContext = new PaymentOrchestrationContext(
                context,
                fintechUserID,
                fintechRedirectURLOK,
                fintechRedirectURLNOK,
                xRequestID,
                paymentProduct,
                body,
                fintechID,
                serviceSessionPassword,
                fintechDataPassword,
                bankProfileID,
                xPsuAuthenticationRequired,
                xProtocolConfiguration,
                fintechDecoupledPreferred,
                fintechBrandLoggingInformation,
                fintechNotificationURI,
                fintechNotificationContentPreferred
        );

        // Step 1: Initiate payment.
        // This is the first asynchronous call in the chain.
        return paymentService.execute(InitiateSinglePaymentRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                .uaContext(context)
                                .authorization(fintechID)
                                .sessionPassword(PasswordExtractingUtil.getDataProtectionPassword(serviceSessionPassword, fintechDataPassword))
                                .fintechUserId(fintechUserID)
                                .fintechRedirectUrlOk(fintechRedirectURLOK)
                                .fintechRedirectUrlNok(fintechRedirectURLNOK)
                                .requestId(xRequestID)
                                .bankProfileId(bankProfileID)
                                .anonymousPsu(xPsuAuthenticationRequired != null && !xPsuAuthenticationRequired)
                                .fintechDecoupledPreferred(fintechDecoupledPreferred != null && !fintechDecoupledPreferred)
                                .fintechBrandLoggingInformation(fintechBrandLoggingInformation)
                                .fintechNotificationURI(fintechNotificationURI)
                                .fintechNotificationContentPreferred(fintechNotificationContentPreferred)
                                .build())
                        .singlePayment(paymentRestRequestBodyToSinglePaymentMapper.map(body, PaymentProductDetails.fromValue(paymentProduct)))
                        .extras(getExtras(xProtocolConfiguration))
                        .build())
                // Apply a mapping function to the result of the payment initiation.
                .thenApply(result -> mapper.translate(result, paymentFacadeResponseBodyToRestBodyMapper))
                // Step 2: Handle the payment response, extract details, and perform PSU login.
                // This thenCompose passes the paymentResponse and the initial orchestrationContext
                // to the helper method, which returns a CompletableFuture of an updated context.
                .thenCompose(paymentResponse ->
                        handlePaymentResponseAndPsuLogin(paymentResponse, orchestrationContext))
                // Step 3: Trigger authorization state and then update authorization.
                // This thenCompose receives the updated context (after PSU login).
                // Inside this lambda, we chain two more CompletableFutures:
                // a) triggerAuthorizationState: takes the context and returns auth state result.
                .thenCompose(contextAfterPsuLogin ->
                        triggerAuthorizationState(contextAfterPsuLogin).thenApply((FacadeResult<AuthStateBody> result) -> redirectionOnlyToOkMapper.translate(result, authStateMapper))
                                .thenApply(result -> {
                                    String redirectCode = result.getHeaders().getFirst("X-XSRF-TOKEN");
                                    return contextAfterPsuLogin.withRedirectCode(redirectCode);

                                })
                );


    }

    /**
     * Updates the authorization based on the current state and orchestration context.
     * This method is called after the authorization state has been triggered.
     *
     * @param context The orchestration context containing all necessary details for the update.
     * @return A CompletableFuture that resolves to the result of the update authorization service.
     */
    private CompletableFuture updateAuthorization(
            CompletableFuture<PaymentOrchestrationContext> context
    ) throws ExecutionException, InterruptedException {

            AuthorizationRequest updateRequest = AuthorizationRequest.builder()
                    .facadeServiceable(FacadeServiceableRequest.builder()
                            .uaContext(context.get().getUaContext())
                            .redirectCode(context.get().getRedirectCode())
                            .authorizationSessionId(context.get().getAuthId().toString())
                            .authorization(context.get().getFintechID())
                            .authorizationKey(context.get().getSessionKey())
                            .bankProfileId(context.get().getBankProfileID())
                            .requestId(context.get().getXRequestID())
                            .sessionPassword(PasswordExtractingUtil.getDataProtectionPassword(context.get().getServiceSessionPassword(), context.get().getFintechDataPassword()))
                            .fintechUserId(context.get().getFintechUserID())
                            .fintechRedirectUrlOk(context.get().getFintechRedirectURLOK())
                            .fintechRedirectUrlNok(context.get().getFintechRedirectURLNOK())
                            .anonymousPsu(null != context.get().getXPsuAuthenticationRequired() && !context.get().getXPsuAuthenticationRequired())
                            .fintechDecoupledPreferred(null != context.get().getFintechDecoupledPreferred() && !context.get().getFintechDecoupledPreferred())
                            .fintechBrandLoggingInformation(context.get().getFintechBrandLoggingInformation())
                            .fintechNotificationURI(context.get().getFintechNotificationURI())
                            .fintechNotificationContentPreferred(context.get().getFintechNotificationContentPreferred())
                            .build())
                    .aisConsent(null == context.get().getBody().getPsuAuthRequest().getConsentAuth() ? null : aisConsentMapper.map(context.get().getBody()))
                    .scaAuthenticationData(context.get().getBody().getPsuAuthRequest().getScaAuthenticationData())
                    .extras(extrasMapper.map(context.get().getBody().getPsuAuthRequest().getExtras()))
                    .build();

            return updateAuthorizationService.execute(updateRequest)
                    .thenApply(stateResult -> mapper.translate(stateResult, updateAuthBodyToApiMapper))
                    .thenApply(updateResult -> {
                        // Extract the new XSRF token from the response headers
                         String newXsrfToken = updateResult.getHeaders().getFirst("Redirect-Code");
                         return newXsrfToken;

                    });

    }

    /**
     * Helper method to build an AuthorizationRequest object.
     *
     * @param redirectCode                       The redirect code.
     * @param sessionKey                         The session key.
     * @param fintechID                          The ID of the fintech.
     * @param fintechUserID                      The ID of the fintech user.
     * @param xRequestID                         The unique request ID.
     * @param bankProfileID                      The ID of the bank profile.
     * @param serviceSessionPassword             Password for the service session.
     * @param fintechDataPassword                Password for fintech data.
     * @param fintechRedirectURLOK               The redirect URL for successful operations.
     * @param fintechRedirectURLNOK              The redirect URL for failed operations.
     * @param xPsuAuthenticationRequired         Indicates if PSU authentication is required.
     * @param fintechDecoupledPreferred          Indicates if decoupled authentication is preferred.
     * @param fintechBrandLoggingInformation     Branding information for logging.
     * @param fintechNotificationURI             URI for fintech notifications.
     * @param fintechNotificationContentPreferred Preferred content type for notifications.
     * @param context                            The UserAgentContext.
     * @param authId                             The authorization session ID.
     * @return A configured AuthorizationRequest.
     */
    private AuthorizationRequest buildAuthRequest(String redirectCode,
                                                  String sessionKey,
                                                  String fintechID,
                                                  String fintechUserID,
                                                  UUID xRequestID,
                                                  UUID bankProfileID,
                                                  String serviceSessionPassword,
                                                  String fintechDataPassword,
                                                  String fintechRedirectURLOK,
                                                  String fintechRedirectURLNOK,
                                                  Boolean xPsuAuthenticationRequired,
                                                  Boolean fintechDecoupledPreferred,
                                                  String fintechBrandLoggingInformation,
                                                  String fintechNotificationURI,
                                                  String fintechNotificationContentPreferred,
                                                  UserAgentContext context,
                                                  UUID authId) {
        return AuthorizationRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                        .uaContext(context)

                        .redirectCode(redirectCode)
                        .authorizationSessionId(authId.toString())
                        .authorization(fintechID)
                        .authorizationKey(sessionKey)
                        .bankProfileId(bankProfileID)
                        .requestId(xRequestID)
                        .sessionPassword(PasswordExtractingUtil.getDataProtectionPassword(serviceSessionPassword, fintechDataPassword))
                        .fintechUserId(fintechUserID)
                        .fintechRedirectUrlOk(fintechRedirectURLOK)
                        .fintechRedirectUrlNok(fintechRedirectURLNOK)
                        .anonymousPsu(xPsuAuthenticationRequired != null && !xPsuAuthenticationRequired)
                        .fintechDecoupledPreferred(fintechDecoupledPreferred != null && !fintechDecoupledPreferred)
                        .fintechBrandLoggingInformation(fintechBrandLoggingInformation)
                        .fintechNotificationURI(fintechNotificationURI)
                        .fintechNotificationContentPreferred(fintechNotificationContentPreferred)
                        .build())
                .build();
    }

    private CompletableFuture<ResponseEntity<?>> refreshAuthorizationStateAfterUpdate(PaymentOrchestrationContext context) {
        AuthorizationRequest authRequest = buildAuthRequest(
                context.getRedirectCode(),
                context.getSessionKey(),
                context.getFintechID(),
                context.getFintechUserID(),
                context.getXRequestID(),
                context.getBankProfileID(),
                context.getServiceSessionPassword(),
                context.getFintechDataPassword(),
                context.getFintechRedirectURLOK(),
                context.getFintechRedirectURLNOK(),
                context.getXPsuAuthenticationRequired(),
                context.getFintechDecoupledPreferred(),
                context.getFintechBrandLoggingInformation(),
                context.getFintechNotificationURI(),
                context.getFintechNotificationContentPreferred(),
                context.getUaContext(),
                context.getAuthId()
        );

        return authorizationStateService.execute(authRequest)
                .thenApply(stateResult -> mapper.translate(stateResult, authStateMapper));
    }


    private Map<ExtraRequestParam, Object> getExtras(String protocolConfiguration) {
        Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
        if (protocolConfiguration != null) {
            extras.put(ExtraRequestParam.PROTOCOL_CONFIGURATION, protocolConfiguration);
        }
        return extras;
    }


    private String extractRedirectCode(String location) {
        if (location != null && location.contains("redirectCode=")) {
            return location.split("redirectCode=")[1];
        }
        throw new IllegalStateException("Missing redirect code in Location header: " + location);
    }


    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface PaymentRestRequestBodyToSinglePaymentMapper {
        SinglePaymentBody map(PaymentInitiation body, PaymentProductDetails paymentProduct);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface AisConsentMapper {

        @Mapping(target = "access", source = "request.psuAuthRequest.consentAuth.consent.access")
        @Mapping(target = "frequencyPerDay", source = "request.psuAuthRequest.consentAuth.consent.frequencyPerDay")
        @Mapping(target = "recurringIndicator", source = "request.psuAuthRequest.consentAuth.consent.recurringIndicator")
        @Mapping(target = "validUntil", source = "request.psuAuthRequest.consentAuth.consent.validUntil")
        @Mapping(target = "combinedServiceIndicator", source = "request.psuAuthRequest.consentAuth.consent.combinedServiceIndicator")
        AisConsent map(PaymentInitiation request);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface PaymentFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<PaymentInitiationResponse, SinglePaymentBody> {
        PaymentInitiationResponse map(SinglePaymentBody facadeEntity);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
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

        default PaymentProduct mapToProduct(PaymentProductDetails value) {
            if (value == null) {
                return null;
            }
            return PaymentProduct.fromValue(value.name());
        }
    }
}

@Value
class PaymentOrchestrationContext {
    // Extracted values from initial steps or obtained during the flow
    UUID authId;
    String redirectCode;
    String xsrfToken;
    String sessionKey;

    // Original request parameters (copied from initiatePayment method arguments)
    UserAgentContext uaContext;
    String fintechUserID;
    String fintechRedirectURLOK;
    String fintechRedirectURLNOK;
    UUID xRequestID;
    String paymentProduct;
    PaymentInitiation body;
    String fintechID;
    String serviceSessionPassword;
    String fintechDataPassword;
    UUID bankProfileID;
    Boolean xPsuAuthenticationRequired;
    String xProtocolConfiguration;
    Boolean fintechDecoupledPreferred;
    String fintechBrandLoggingInformation;
    String fintechNotificationURI;
    String fintechNotificationContentPreferred;

    /**
     * Constructor to create an initial context before authId, redirectCode, xsrfToken,
     * and sessionKey are known. These will be null initially.
     */
    PaymentOrchestrationContext(
            UserAgentContext uaContext,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String paymentProduct,
            PaymentInitiation body,
            String fintechID,
            String serviceSessionPassword,
            String fintechDataPassword,
            UUID bankProfileID,
            Boolean xPsuAuthenticationRequired,
            String xProtocolConfiguration,
            Boolean fintechDecoupledPreferred,
            String fintechBrandLoggingInformation,
            String fintechNotificationURI,
            String fintechNotificationContentPreferred
    ) {
        this(null, null, null, null, // Initial nulls for auth details
                uaContext, fintechUserID, fintechRedirectURLOK, fintechRedirectURLNOK, xRequestID,
                paymentProduct, body, fintechID, serviceSessionPassword, fintechDataPassword,
                bankProfileID, xPsuAuthenticationRequired, xProtocolConfiguration,
                fintechDecoupledPreferred, fintechBrandLoggingInformation, fintechNotificationURI,
                fintechNotificationContentPreferred);
    }

    /**
     * Private constructor used internally to create new instances of the context
     * with updated values, especially after obtaining auth details.
     */
    private PaymentOrchestrationContext(
            UUID authId, String redirectCode, String xsrfToken, String sessionKey,
            UserAgentContext uaContext,
            String fintechUserID, String fintechRedirectURLOK, String fintechRedirectURLNOK,
            UUID xRequestID, String paymentProduct, PaymentInitiation body,
            String fintechID, String serviceSessionPassword, String fintechDataPassword,
            UUID bankProfileID, Boolean xPsuAuthenticationRequired, String xProtocolConfiguration,
            Boolean fintechDecoupledPreferred, String fintechBrandLoggingInformation,
            String fintechNotificationURI, String fintechNotificationContentPreferred
    ) {
        this.authId = authId;
        this.redirectCode = redirectCode;
        this.xsrfToken = xsrfToken;
        this.sessionKey = sessionKey;
        this.uaContext = uaContext;
        this.fintechUserID = fintechUserID;
        this.fintechRedirectURLOK = fintechRedirectURLOK;
        this.fintechRedirectURLNOK = fintechRedirectURLNOK;
        this.xRequestID = xRequestID;
        this.paymentProduct = paymentProduct;
        this.body = body;
        this.fintechID = fintechID;
        this.serviceSessionPassword = serviceSessionPassword;
        this.fintechDataPassword = fintechDataPassword;
        this.bankProfileID = bankProfileID;
        this.xPsuAuthenticationRequired = xPsuAuthenticationRequired;
        this.xProtocolConfiguration = xProtocolConfiguration;
        this.fintechDecoupledPreferred = fintechDecoupledPreferred;
        this.fintechBrandLoggingInformation = fintechBrandLoggingInformation;
        this.fintechNotificationURI = fintechNotificationURI;
        this.fintechNotificationContentPreferred = fintechNotificationContentPreferred;
    }

    /**
     * Creates a new PaymentOrchestrationContext instance with updated authentication details.
     * This method ensures the immutability of the context object.
     *
     * @param authId       The authorization session ID.
     * @param redirectCode The redirect code.
     * @param xsrfToken    The XSRF token.
     * @param sessionKey   The session key obtained after PSU login.
     * @return A new PaymentOrchestrationContext instance with updated auth details.
     */
    public PaymentOrchestrationContext withAuthDetails(UUID authId, String redirectCode, String xsrfToken, String sessionKey) {
        return new PaymentOrchestrationContext(
                authId, redirectCode, xsrfToken, sessionKey,
                this.uaContext, this.fintechUserID, this.fintechRedirectURLOK, this.fintechRedirectURLNOK,
                this.xRequestID, this.paymentProduct, this.body, this.fintechID, this.serviceSessionPassword,
                this.fintechDataPassword, this.bankProfileID, this.xPsuAuthenticationRequired,
                this.xProtocolConfiguration, this.fintechDecoupledPreferred,
                this.fintechBrandLoggingInformation, this.fintechNotificationURI,
                this.fintechNotificationContentPreferred
        );
    }
    public PaymentOrchestrationContext withRedirectCode(String redirectCode) {
        return new PaymentOrchestrationContext(
                this.authId, redirectCode, this.xsrfToken, this.sessionKey,
                this.uaContext, this.fintechUserID, this.fintechRedirectURLOK, this.fintechRedirectURLNOK,
                this.xRequestID, this.paymentProduct, this.body, this.fintechID, this.serviceSessionPassword,
                this.fintechDataPassword, this.bankProfileID, this.xPsuAuthenticationRequired,
                this.xProtocolConfiguration, this.fintechDecoupledPreferred,
                this.fintechBrandLoggingInformation, this.fintechNotificationURI,
                this.fintechNotificationContentPreferred
        );
    }
}
