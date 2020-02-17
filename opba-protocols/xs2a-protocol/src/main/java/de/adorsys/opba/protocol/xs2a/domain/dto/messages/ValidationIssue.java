package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ValidationIssue extends InternalProcessResult {

    @NonNull
    private URI provideMoreParamsDialog;

    @Builder
    public ValidationIssue(String processId, String executionId, Object result, URI provideMoreParamsDialog) {
        super(processId, executionId, result);
        this.provideMoreParamsDialog = provideMoreParamsDialog;
    }
}
