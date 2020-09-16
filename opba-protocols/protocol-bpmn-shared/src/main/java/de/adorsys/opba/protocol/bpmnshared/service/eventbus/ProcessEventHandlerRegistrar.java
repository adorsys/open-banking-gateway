package de.adorsys.opba.protocol.bpmnshared.service.eventbus;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.Redirect;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalReturnableProcessError;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ValidationProblem;
import de.adorsys.opba.protocol.bpmnshared.outcome.OutcomeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Naive implementation of internal event bus. Acts as a broker.
 * This class adds handlers for the events that are emitted by BPMN engine.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessEventHandlerRegistrar {

    private final ProcessResultEventHandler handler;

    /**
     * Adds handler for BPMN event.
     * @param processId BPMN process id event source. BPMN can have multiple executions of same process, this is
     *                  the id of the process that identifies the execution uniquely.
     * @param mapper Mapper to transform internal event that is sent by BPMN to higher-level result, i.e. to
     *               {@link de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result} that is expected by
     *               an entrypoint that triggered the process.
     * @param <T> Expected result class. This class will be mapped from internal process result
     * ({@link de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalProcessResult}) by {@code mapper}
     */
    public <T> void addHandler(String processId, OutcomeMapper<T> mapper) {
        handler.add(
                processId,
                procResult -> {
                    if (procResult instanceof ProcessResponse) {
                        mapper.onSuccess((ProcessResponse) procResult);
                    } else if (procResult instanceof Redirect) {
                        mapper.onRedirect((Redirect) procResult);
                    } else if (procResult instanceof ValidationProblem) {
                        mapper.onValidationProblem((ValidationProblem) procResult);
                    } else if (procResult instanceof ConsentAcquired) {
                        mapper.onConsentAcquired((ConsentAcquired) procResult);
                    } else if (procResult instanceof InternalReturnableProcessError) {
                        mapper.onReturnableProcessError((InternalReturnableProcessError) procResult);
                    } else {
                        mapper.onError();
                    }
                });
    }
}
