package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import com.google.common.collect.ImmutableSet;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessError;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableExceptionEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.JOB_EXECUTION_FAILURE;
import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.PROCESS_COMPLETED_WITH_ERROR_END_EVENT;

@Slf4j
@Configuration
public class FlowableJobEventListener extends AbstractFlowableEngineEventListener {

    private final ApplicationEventPublisher applicationEventPublisher;

    public FlowableJobEventListener(ApplicationEventPublisher applicationEventPublisher) {
        super(ImmutableSet.of(JOB_EXECUTION_FAILURE, PROCESS_COMPLETED_WITH_ERROR_END_EVENT));
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected void jobExecutionFailure(FlowableEngineEntityEvent event) {
        handleError(event);
    }

    @Override
    protected void processCompletedWithErrorEnd(FlowableEngineEntityEvent event) {
        handleError(event);
    }

    @SneakyThrows
    private void handleError(FlowableEngineEntityEvent event) {
        if (event instanceof FlowableExceptionEvent) {
            var cause = ((FlowableExceptionEvent) event).getCause();
            log.error("Exception occurred for execution {} of process {}", event.getExecutionId(), event.getProcessInstanceId(), cause);
            handleXs2aAdapterError(cause);
        }

        ProcessError result = ProcessError.builder()
                .processId(event.getProcessInstanceId())
                .executionId(event.getExecutionId())
                .message(exceptionMessage(event))
                .build();

        applicationEventPublisher.publishEvent(result);
    }

    private void handleXs2aAdapterError(Throwable cause) throws IllegalAccessException, InvocationTargetException {
        if (null != cause) {
            var method = ReflectionUtils.findMethod(cause.getClass(), "getStatusCode");
            if (null != method) {
                log.error("Response status code: {}", method.invoke(cause));
            }
        }
    }

    private String exceptionMessage(FlowableEngineEntityEvent event) {
        if (event instanceof FlowableExceptionEvent) {
            return "An exception occurred";
        }

        return "An error occurred";
    }
}
