package de.adorsys.opba.core.protocol.config.flowable;

import com.google.common.collect.ImmutableSet;
import de.adorsys.opba.core.protocol.domain.dto.messages.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.PROCESS_COMPLETED;

@Slf4j
@Configuration
public class FlowableJobEventListener extends AbstractFlowableEngineEventListener {

    private final ApplicationEventPublisher applicationEventPublisher;

    public FlowableJobEventListener(ApplicationEventPublisher applicationEventPublisher) {
        super(ImmutableSet.of(PROCESS_COMPLETED));
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        ResponseResult result = new ResponseResult();
        result.setProcessId(event.getProcessInstanceId());
        applicationEventPublisher.publishEvent(result);
    }
}
