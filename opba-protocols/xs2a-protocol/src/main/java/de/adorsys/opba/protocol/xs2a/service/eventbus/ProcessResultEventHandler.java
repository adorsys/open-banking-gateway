package de.adorsys.opba.protocol.xs2a.service.eventbus;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.InternalProcessResult;
import lombok.RequiredArgsConstructor;
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
            consumer = subscribers.remove(result.getProcessId());

            if (null == consumer) {
                deadLetterQueue.put(result.getProcessId(), result);
                return;
            }
        }

        consumer.accept(result);
    }
}
