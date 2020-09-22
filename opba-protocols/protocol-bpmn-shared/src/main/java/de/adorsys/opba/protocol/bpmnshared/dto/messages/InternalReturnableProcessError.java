package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalReturnableProcessError extends ProcessErrorWithRootProcessId {
    private ProcessErrorEnum processErrorEnum;
    public InternalReturnableProcessError(String rootProcessId, String executionId, ProcessErrorEnum processErrorEnum) {
        super(rootProcessId, executionId, processErrorEnum.toString(), true);
        this.processErrorEnum = processErrorEnum;
        log.debug("create ReturnableProcessError {} {} {}", rootProcessId, executionId, processErrorEnum.toString());
    }
}
