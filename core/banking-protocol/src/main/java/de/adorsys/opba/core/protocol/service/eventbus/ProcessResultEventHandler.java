package de.adorsys.opba.core.protocol.service.eventbus;

import de.adorsys.opba.core.protocol.domain.dto.ProcessResult;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
