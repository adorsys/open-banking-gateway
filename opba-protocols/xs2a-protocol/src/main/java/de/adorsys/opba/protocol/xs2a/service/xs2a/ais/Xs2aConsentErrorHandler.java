package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessErrorEnum;
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
            tryHandleConsentExceededException(execution, ex, eventPublisher);
        }
    }

    private void tryHandleConsentExceededException(DelegateExecution execution, ErrorResponseException ex, ApplicationEventPublisher eventPublisher) {
        if (!ex.getErrorResponse().isPresent() || null == ex.getErrorResponse().get().getTppMessages()) {
            throw ex;
        }

        if (isConsentExceeded(ex)) {
            eventPublisher.publishEvent(new InternalReturnableProcessError(execution.getRootProcessInstanceId(), execution.getId(),
                ProcessErrorEnum.CONSENT_ACCESS_EXCEEDED_LIMIT));
            return;
        }
        throw ex;
    }

    private boolean isConsentExceeded(ErrorResponseException ex) {
        try {
            Gson gson = new Gson();
            String message = ex.getMessage();
            JsonObject object = gson.fromJson(message, JsonObject.class);
            JsonArray tppMessages = object.getAsJsonArray("tppMessages");
            for (int i = 0; i < tppMessages.size(); i++) {
                JsonObject messageObject = tppMessages.get(i).getAsJsonObject();
                if (messageObject != null) {
                    JsonElement messageCodeElement = messageObject.get("code");
                    if (messageCodeElement != null) {
                        if ("ACCESS_EXCEEDED".equalsIgnoreCase(messageCodeElement.getAsString())) {
                            return true;
                        } else {
                            log.debug("message {} did not contain expected code", message);
                        }
                    } else {
                        log.debug("message {} had no code element", message);
                    }
                } else {
                    log.debug("message {} had unknown element", message);
                }
            }
        } catch (Exception ex2) {
            ex2.printStackTrace();
            log.error("exception {} during parsing exception {}", ex2.getMessage(), ex.getMessage());
        }
        return false;
    }
}
