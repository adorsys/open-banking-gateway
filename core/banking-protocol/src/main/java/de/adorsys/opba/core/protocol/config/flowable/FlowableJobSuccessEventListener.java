package de.adorsys.opba.core.protocol.config.flowable;

import com.google.common.collect.ImmutableSet;
import de.adorsys.opba.core.protocol.domain.dto.ResponseResult;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.PROCESS_COMPLETED;

@Configuration
public class FlowableJobSuccessEventListener extends AbstractFlowableEngineEventListener {

    private final ApplicationEventPublisher applicationEventPublisher;

    public FlowableJobSuccessEventListener(ApplicationEventPublisher applicationEventPublisher) {
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
