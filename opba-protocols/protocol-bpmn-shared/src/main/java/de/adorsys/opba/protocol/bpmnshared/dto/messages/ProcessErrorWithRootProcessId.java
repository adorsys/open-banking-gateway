package de.adorsys.opba.protocol.bpmnshared.dto.messages;

public class ProcessErrorWithRootProcessId extends ProcessError {

    public ProcessErrorWithRootProcessId(String processId, String executionId, String message, boolean canRedirectBackToFintech) {
        super(processId, executionId, message, canRedirectBackToFintech);
    }
}
