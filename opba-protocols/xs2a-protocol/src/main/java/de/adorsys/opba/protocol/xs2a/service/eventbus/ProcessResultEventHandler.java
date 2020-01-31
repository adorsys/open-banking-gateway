package de.adorsys.opba.protocol.xs2a.service.eventbus;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ProcessResult;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Allows consumer to be registered after event addressed to him is published.
 * There can be only one undelivered message for unregistered consumer, multiple messages are
 * not supported.
 */
@Service
public class ProcessResultEventHandler {

    private final Object lock = new Object();

    private final Map<String, Consumer<ProcessResult>> subscribers = new HashMap<>();
    private final Map<String, ProcessResult> deadLetterQueue = new HashMap<>();

    public void add(String processId, Consumer<ProcessResult> subscriber) {
        ProcessResult delayedMessage;

        synchronized (lock) {
            delayedMessage = deadLetterQueue.remove(processId);
            if (null == delayedMessage) {
                subscribers.put(processId, subscriber);
                return;
            }
        }

        subscriber.accept(delayedMessage);
    }

    @EventListener
    public void handleEvent(ProcessResult result) {
        Consumer<ProcessResult> consumer;

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
