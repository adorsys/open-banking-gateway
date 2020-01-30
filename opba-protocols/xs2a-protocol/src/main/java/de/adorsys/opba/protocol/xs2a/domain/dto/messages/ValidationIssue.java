package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;

@Getter
public class ValidationIssue extends InternalProcessResult {

    @NonNull
    private URI provideMoreParamsDialog;

    @Builder
    public ValidationIssue(String processId, Object result, URI provideMoreParamsDialog) {
        super(processId, result);
        this.provideMoreParamsDialog = provideMoreParamsDialog;
    }
}
