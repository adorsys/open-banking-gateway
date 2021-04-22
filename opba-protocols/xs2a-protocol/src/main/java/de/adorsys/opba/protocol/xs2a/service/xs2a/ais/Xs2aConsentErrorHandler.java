package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.adorsys.opba.protocol.api.errors.ProcessErrorStrings;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalReturnableProcessError;
import de.adorsys.xs2a.adapter.api.exception.ErrorResponseException;
import de.adorsys.xs2a.adapter.api.model.MessageCode;
import de.adorsys.xs2a.adapter.api.model.TppMessage;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Xs2aConsentErrorHandler {
    public void tryActionOrHandleConsentErrors(DelegateExecution execution, ApplicationEventPublisher eventPublisher, Runnable tryCreate) {
        try {
            tryCreate.run();
        } catch (ErrorResponseException ex) {
            tryHandleConsentException(execution, ex, eventPublisher);
        }
    }

    private void tryHandleConsentException(DelegateExecution execution, ErrorResponseException ex, ApplicationEventPublisher eventPublisher) {
        if (!ex.getErrorResponse().isPresent() || null == ex.getErrorResponse().get().getTppMessages()) {
            throw ex;
        }

        if (isTppMessage(ex, MessageCode.ACCESS_EXCEEDED)) {
            eventPublisher.publishEvent(new InternalReturnableProcessError(execution.getRootProcessInstanceId(), execution.getId(),
                ProcessErrorStrings.CONSENT_ACCESS_EXCEEDED_LIMIT));
            return;
        }
        if (isTppMessage(ex, MessageCode.CONSENT_UNKNOWN)) {
            eventPublisher.publishEvent(new InternalReturnableProcessError(execution.getRootProcessInstanceId(), execution.getId(),
                ProcessErrorStrings.CONSENT_UNKNOWN));
            return;
        }
        if (isTppMessage(ex, MessageCode.CONSENT_EXPIRED)) {
            eventPublisher.publishEvent(new InternalReturnableProcessError(execution.getRootProcessInstanceId(), execution.getId(),
                ProcessErrorStrings.CONSENT_EXPIRED));
            return;
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
