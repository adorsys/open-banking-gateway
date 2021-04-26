package de.adorsys.opba.protocol.hbci.entrypoint.authorization;

import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.ChallengeData;
import de.adorsys.opba.protocol.api.dto.request.OtpFormat;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.AuthRequestData;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.bpmnshared.dto.ContextBasedValidationErrorResult;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.LastViolations;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.LAST_VALIDATION_ISSUES;

/**
 * TODO - make shared
 * Entry point to get list of the required inputs from the user.
 */
@Service("hbciGetAuthorizationState")
@RequiredArgsConstructor
public class HbciGetAuthorizationState implements GetAuthorizationState {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final HbciViolationsMapper violationsMapper;
    private final HbciScaMethodsMapper scaMethodsMapper;
    private final PaymentBodyMapper pisBodyMapper;
    private final AisConsentBodyMapper aisBodyMapper;
    private final ChallengeDataMapper challengeDataMapper;

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();

        AuthStateBody result;
        if (null != runtimeService.createExecutionQuery().executionId(executionId).singleResult()) {
            result = readFromRuntime(executionId);
        } else {
            result = readFromHistory(executionId);
        }

        return CompletableFuture.completedFuture(
                new ContextBasedValidationErrorResult<>(
                        null == result.getRedirectTo() ? null : URI.create(result.getRedirectTo()),
                        executionId,
                        result
                )
        );
    }

    private AuthStateBody readFromRuntime(String executionId) {
        HbciContext ctx = (HbciContext) runtimeService.getVariable(executionId, CONTEXT);

        // Whatever is non-null - that takes precedence
        return buildBody(
                ctx,
                null == ctx.getViolations() || ctx.getViolations().isEmpty()
                        ? (LastViolations) runtimeService.getVariable(executionId, LAST_VALIDATION_ISSUES)
                        : new LastViolations(ctx.getViolations()),
                null == ctx.getLastRedirection()
                        ? (LastRedirectionTarget) runtimeService.getVariable(executionId, LAST_REDIRECTION_TARGET)
                        : ctx.getLastRedirection()
        );
    }

    private AuthStateBody readFromHistory(String executionId) {
        // Ended processes has very coarse information:
        HistoricActivityInstance finished = historyService.createHistoricActivityInstanceQuery()
                .executionId(executionId)
                .finished()
                .listPage(0, 1)
                .get(0);

        HbciContext ctx = (HbciContext) historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(finished.getProcessInstanceId())
                .variableName(CONTEXT)
                .singleResult()
                .getValue();

        return buildBody(ctx, new LastViolations(ctx.getViolations()), ctx.getLastRedirection());
    }

    private AuthStateBody buildBody(HbciContext ctx,  LastViolations issues, LastRedirectionTarget redirectionTarget) {
        ProtocolAction action = ctx.getAction();
        List<ScaMethod> scaMethods = ctx.getAvailableSca();
        String redirectTo = null == redirectionTarget ? null : redirectionTarget.getRedirectTo();

        AuthRequestData authRequestData = AuthRequestData.builder()
                .aisConsent(ctx instanceof AccountListHbciContext || ctx instanceof TransactionListHbciContext ? aisBodyMapper.map(ctx.getHbciDialogConsent()) : null)
                .singlePaymentBody(ctx instanceof PaymentHbciContext ? pisBodyMapper.map(((PaymentHbciContext) ctx).getPayment()) : null)
                .bankName(ctx.getRequestScoped().aspspProfile().getName())
                .fintechName(ctx.getRequestScoped().fintechProfile().getName())
                .build();

        return new AuthStateBody(
                action.name(),
                violationsMapper.map(issues.getViolations()),
                scaMethodsMapper.map(scaMethods),
                redirectTo,
                authRequestData,
                challengeDataMapper.map(ctx.getChallengeData())
        );
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciViolationsMapper {
        Set<ValidationError> map(Set<ValidationIssue> from);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciScaMethodsMapper {
        Set<ScaMethod> map(List<ScaMethod> from);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface PaymentBodyMapper extends DtoMapper<PaymentInitiateBody, SinglePaymentBody> {
        SinglePaymentBody map(PaymentInitiateBody paymentInitiateBody);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface AisConsentBodyMapper extends DtoMapper<HbciConsent, AisConsent> {
        AisConsent map(HbciConsent hbciConsent);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface ChallengeDataMapper extends DtoMapper<de.adorsys.multibanking.domain.ChallengeData, ChallengeData> {
        default byte[] toBytes(String string) {
            return string != null ? Base64.getDecoder().decode(string) : null;
        }

        default OtpFormat toEnum(String code) {
            for (OtpFormat otpFormat : OtpFormat.values()) {
                if (otpFormat.name().equals(code)) {
                    return otpFormat;
                }
            }
            return null;
        }
    }
}
