package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.bpmnshared.dto.ContextBasedValidationErrorResult;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.context.LastRedirectionTarget;
import de.adorsys.opba.protocol.xs2a.context.LastViolations;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.entrypoint.helpers.Xs2aUuidMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateBody;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_REDIRECTION_TARGET;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.LAST_VALIDATION_ISSUES;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Entry point to get list of the required inputs from the user. BPMN engine and process is not touched.
 */
@Service("xs2aGetAuthorizationState")
@RequiredArgsConstructor
public class Xs2aGetAuthorizationState implements GetAuthorizationState {

    private final PaymentBodyMapper pisBodyMapper;
    private final AisConsentBodyMapper aisBodyMapper;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final ViolationsMapper violationsMapper;
    private final ScaMethodsMapper scaMethodsMapper;

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
        Xs2aContext ctx = (Xs2aContext) runtimeService.getVariable(executionId, CONTEXT);

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

        Xs2aContext ctx = (Xs2aContext) historyService.createHistoricVariableInstanceQuery()
                                                .processInstanceId(finished.getProcessInstanceId())
                                                .variableName(CONTEXT)
                                                .singleResult()
                                                .getValue();

        return buildBody(ctx, new LastViolations(ctx.getViolations()), ctx.getLastRedirection());
    }

    private AuthStateBody buildBody(Xs2aContext ctx, LastViolations issues, LastRedirectionTarget redirectionTarget) {

        ProtocolAction action = ctx.getAction();
        List<ScaMethod> scaMethods = ctx.getAvailableSca();
        String redirectTo = null == redirectionTarget ? null : redirectionTarget.getRedirectTo();

        Object resultBody = null;

        if (ctx instanceof Xs2aPisContext) {
            resultBody = pisBodyMapper.map(((Xs2aPisContext) ctx).getPayment());
        } else if (ctx instanceof Xs2aAisContext) {
            resultBody = aisBodyMapper.map(((Xs2aAisContext) ctx).getAisConsent());
        }

        return new AuthStateBody(
                action.name(),
                violationsMapper.map(issues.getViolations()),
                scaMethodsMapper.map(scaMethods),
                redirectTo,
                resultBody
        );
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ViolationsMapper extends DtoMapper<Set<ValidationIssue>, Set<ValidationError>> {
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ScaMethodsMapper extends DtoMapper<List<ScaMethod>, Set<de.adorsys.opba.protocol.api.dto.result.body.ScaMethod>> {
    }

    @Mapper(componentModel = SPRING_KEYWORD, uses = Xs2aUuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface PaymentBodyMapper extends DtoMapper<PaymentInitiateBody, SinglePaymentBody> {
        SinglePaymentBody map(PaymentInitiateBody paymentInitiateBody);
    }

    @Mapper(componentModel = SPRING_KEYWORD, uses = Xs2aUuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface AisConsentBodyMapper extends DtoMapper<AisConsentInitiateBody, AisConsent> {
        AisConsent map(AisConsentInitiateBody aisConsentInitiateBody);

        default List<String> map(List<AisConsentInitiateBody.AccountReferenceBody> accounts) {
            if (accounts == null || accounts.isEmpty()) {
                return null;
            }
            return accounts.stream()
                           .map(AisConsentInitiateBody.AccountReferenceBody::getIban)
                           .collect(Collectors.toList());
        }
    }
}
