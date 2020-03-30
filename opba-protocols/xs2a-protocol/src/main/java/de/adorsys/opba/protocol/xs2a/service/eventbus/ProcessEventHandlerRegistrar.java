package de.adorsys.opba.protocol.xs2a.service.eventbus;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Redirect;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Response;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ValidationProblem;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessEventHandlerRegistrar {

    private final ProcessResultEventHandler handler;

    public <T> void addHandler(String processId, OutcomeMapper<T> mapper) {
        handler.add(
                processId,
                procResult -> {
                    if (procResult instanceof Response) {
                        mapper.onSuccess((Response) procResult);
                    } else if (procResult instanceof Redirect) {
                        mapper.onRedirect((Redirect) procResult);
                    } else if (procResult instanceof ValidationProblem) {
                        mapper.onValidationProblem((ValidationProblem) procResult);
                    } else if (procResult instanceof ConsentAcquired) {
                        mapper.onConsentAcquired((ConsentAcquired) procResult);
                    } else {
                        mapper.onError();
                    }
                });
    }
}
