package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.adorsys.opba.protocol.api.errors.ProcessErrorStrings;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalReturnableProcessError;
import de.adorsys.xs2a.adapter.service.exception.ErrorResponseException;
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

        if (isTppMessage(ex, "ACCESS_EXCEEDED")) {
            eventPublisher.publishEvent(new InternalReturnableProcessError(execution.getRootProcessInstanceId(), execution.getId(),
                ProcessErrorStrings.CONSENT_ACCESS_EXCEEDED_LIMIT));
            return;
        }
        if (isTppMessage(ex, "CONSENT_UNKNOWN")) {
            eventPublisher.publishEvent(new InternalReturnableProcessError(execution.getRootProcessInstanceId(), execution.getId(),
                ProcessErrorStrings.CONSENT_UNKNOWN));
            return;
        }
        throw ex;
    }

    private boolean isTppMessage(ErrorResponseException ex, String searchText) {
        log.debug("I am looking for {} in {}", searchText, ex.getMessage());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String message = ex.getMessage();
            JsonNode object = objectMapper.readTree(message);
            JsonNode tppMessagesNode = object.get("tppMessages");
            if (tppMessagesNode.isArray()) {
                ArrayNode tppMessages = (ArrayNode) tppMessagesNode;
                for (int i = 0; i < tppMessages.size(); i++) {
                    JsonNode singleTppMessageNode = tppMessages.get(i);
                    if (singleTppMessageNode != null) {
                        JsonNode singleTppMessageNodeCode = singleTppMessageNode.get("code");
                        if (singleTppMessageNodeCode != null) {
                            if (searchText.equalsIgnoreCase(singleTppMessageNodeCode.textValue())) {
                                log.error("FOUND ERROR: {}", searchText);
                                return true;
                            }
                        } else {
                            log.warn("error during errorhandling: message {} had no code element", message);
                        }
                    } else {
                        log.warn("error during errorhandling: message {} had unknown element", message);
                    }
                }
            }

        } catch (Exception ex2) {
            ex2.printStackTrace();
            log.error("exception {} during parsing exception {}", ex2.getMessage(), ex.getMessage());
        }
        return false;
    }
}
