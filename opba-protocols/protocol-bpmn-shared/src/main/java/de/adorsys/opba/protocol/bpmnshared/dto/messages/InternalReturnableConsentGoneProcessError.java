package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import de.adorsys.opba.protocol.api.errors.ProcessErrorConsentGone;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalReturnableConsentGoneProcessError extends ProcessErrorWithRootProcessId {

    private final ProcessErrorConsentGone consentGone;

    public InternalReturnableConsentGoneProcessError(String rootProcessId, String executionId, ProcessErrorConsentGone processError) {
        super(rootProcessId, executionId, processError.name(), true);
        this.consentGone = processError;
        log.debug("create ReturnableProcessError {} {} {}", rootProcessId, executionId, processError.name());
    }
}
