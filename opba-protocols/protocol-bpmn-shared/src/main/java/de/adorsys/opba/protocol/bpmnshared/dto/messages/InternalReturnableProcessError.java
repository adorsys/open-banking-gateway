package de.adorsys.opba.protocol.bpmnshared.dto.messages;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalReturnableProcessError extends ProcessError {
    private ProcessErrorEnum processErrorEnum;
    public InternalReturnableProcessError(String processId, String executionId, ProcessErrorEnum processErrorEnum) {
        super(processId, executionId, processErrorEnum.toString(), true);
        this.processErrorEnum = processErrorEnum;
        log.debug("create ReturnableProcessError {} {} {}", processId, executionId, processErrorEnum.toString());
    }
}
