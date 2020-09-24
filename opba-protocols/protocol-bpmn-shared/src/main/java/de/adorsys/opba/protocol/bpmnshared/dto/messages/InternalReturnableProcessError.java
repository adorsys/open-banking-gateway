package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalReturnableProcessError extends ProcessErrorWithRootProcessId {
    private String processErrorString;
    public InternalReturnableProcessError(String rootProcessId, String executionId, String processErrorString) {
        super(rootProcessId, executionId, processErrorString, true);
        this.processErrorString = processErrorString;
        log.debug("create ReturnableProcessError {} {} {}", rootProcessId, executionId, processErrorString);
    }
}
