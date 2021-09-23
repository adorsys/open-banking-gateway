package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.api.model.AuthenticationType;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import lombok.experimental.UtilityClass;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

@UtilityClass
public class DecoupledUtil {

    public void postHandleSca(DelegateExecution execution, Xs2aContext context, ScaStatus scaStatus, ApplicationEventPublisher eventPublisher) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setDecoupledScaFinished(ScaStatus.FINALISED == scaStatus); // TODO Error cases
                    ctx.setScaStatus(scaStatus.toString());
                }
        );

        Optional<AuthenticationObject> scaSelectedOp = Optional.ofNullable(context.getScaSelected());
        eventPublisher.publishEvent(
                new ProcessResponse(
                        execution.getRootProcessInstanceId(),
                        execution.getId(),
                        UpdateAuthBody.builder()
                                .scaStatus(scaStatus.name())
                                .scaAuthenticationType(scaSelectedOp.map(AuthenticationObject::getAuthenticationType).map(AuthenticationType::name).orElse(null))
                                .scaExplanation(scaSelectedOp.map(AuthenticationObject::getExplanation).orElse(null))
                                .build()
                )
        );
    }
}
