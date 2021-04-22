package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalReturnableConsentGoneProcessError;
import de.adorsys.opba.protocol.xs2a.config.aspspmessages.AspspMessages;
import de.adorsys.xs2a.adapter.api.exception.ErrorResponseException;
import de.adorsys.xs2a.adapter.api.model.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Xs2aConsentErrorHandler {

    private final AspspMessages messages;

    public void tryActionOrHandleConsentErrors(DelegateExecution execution, ApplicationEventPublisher eventPublisher, Runnable tryCreate) {
        try {
            tryCreate.run();
        } catch (ErrorResponseException ex) {
            tryHandleConsentException(execution, ex, eventPublisher);
        }
    }

    private void tryHandleConsentException(DelegateExecution execution, ErrorResponseException ex, ApplicationEventPublisher eventPublisher) {
        if (ex.getErrorResponse().isEmpty() || null == ex.getErrorResponse().get().getTppMessages()) {
            throw ex;
        }

        for (var message : messages.getConsentGone().entrySet()) {
            if (isTppMessage(ex, message.getKey())) {
                eventPublisher.publishEvent(new InternalReturnableConsentGoneProcessError(execution.getRootProcessInstanceId(), execution.getId(), message.getValue()));
                return;
            }
        }

        throw ex;
    }

    private boolean isTppMessage(ErrorResponseException ex, MessageCode code) {
        if (ex.getErrorResponse().isEmpty() || null == ex.getErrorResponse().get().getTppMessages()) {
            return false;
        }

        return ex.getErrorResponse().get().getTppMessages().stream().anyMatch(it -> code.equals(it.getCode()));
    }
}
