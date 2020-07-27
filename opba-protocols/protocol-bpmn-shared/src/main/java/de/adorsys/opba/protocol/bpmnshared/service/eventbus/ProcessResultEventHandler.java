package de.adorsys.opba.protocol.bpmnshared.service.eventbus;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalProcessResult;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessError;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Naive implementation of internal event bus. Acts as the event registry.
 * This registers handlers for the events that are emitted by BPMN engine.
 * Allows consumer to be registered after event addressed to him is published.
 * There can be only one undelivered message for unregistered consumer, multiple messages are
 * not supported.
 * FIXME: https://github.com/adorsys/open-banking-gateway/issues/456
 */
@Service
@RequiredArgsConstructor
class ProcessResultEventHandler {

    private final Object lock = new Object();

    private final RuntimeService runtimeService;
    private final Map<String, Consumer<InternalProcessResult>> subscribers;
    private final Map<String, InternalProcessResult> deadLetterQueue;

    /**
     * Adds the subscriber to the BPMN process. If any already exists - old one will be removed.
     * @param processId BPMN process id to subscribe to
     * @param subscriber Internal BPMN event handling function
     */
    void add(String processId, Consumer<InternalProcessResult> subscriber) {
        InternalProcessResult delayedMessage;

        synchronized (lock) {
            delayedMessage = deadLetterQueue.remove(processId);
            if (null == delayedMessage) {
                subscribers.put(processId, subscriber);
                return;
            }
        }

        subscriber.accept(delayedMessage);
    }

    /**
     * Spring event-bus listener to listen for BPMN process result.
     * @param result BPMN process message to notify with the subscribers.
     */
    @TransactionalEventListener
    public void handleEvent(InternalProcessResult result) {
        Consumer<InternalProcessResult> consumer;

        synchronized (lock) {
            InternalProcessResult handledResult = result;

            if (handledResult instanceof ProcessError) {
                handledResult = replaceErrorProcessIdWithParentProcessId((ProcessError) handledResult);
            }

            consumer = subscribers.remove(handledResult.getProcessId());

            if (null == consumer) {
                deadLetterQueue.put(handledResult.getProcessId(), result);
                return;
            }
        }

        consumer.accept(result);
    }


    private ProcessError replaceErrorProcessIdWithParentProcessId(ProcessError error) {
        String rootProcessId = runtimeService.createProcessInstanceQuery()
                .processInstanceId(error.getProcessId())
                .singleResult()
                .getRootProcessInstanceId();

        return error.toBuilder().processId(rootProcessId).build();
    }
}
