package de.adorsys.opba.protocol.xs2a.service.eventbus;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.InternalProcessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Allows consumer to be registered after event addressed to him is published.
 * There can be only one undelivered message for unregistered consumer, multiple messages are
 * not supported.
 * FIXME: https://github.com/adorsys/open-banking-gateway/issues/456
 */
@Service
@RequiredArgsConstructor
public class ProcessResultEventHandler {

    private final Object lock = new Object();

    private final Map<String, Consumer<InternalProcessResult>> subscribers;
    private final Map<String, InternalProcessResult> deadLetterQueue;

    public void add(String processId, Consumer<InternalProcessResult> subscriber) {
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
