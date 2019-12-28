package de.adorsys.opba.core.protocol.domain.dto.messages;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;

@Getter
public class ValidationIssueResult extends ProcessResult {

    @NonNull
    private URI provideMoreParamsDialog;

    @Builder
    public ValidationIssueResult(String processId, Object result, URI provideMoreParamsDialog) {
        super(processId, result);
        this.provideMoreParamsDialog = provideMoreParamsDialog;
    }
}
