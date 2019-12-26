package de.adorsys.opba.core.protocol.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.net.URI;
import java.util.Set;

@Getter
public class ValidationIssueResult extends ProcessResult {

    private URI provideMoreParamsDialog;
    private Set<ValidationIssue> violations;

    @Builder
    public ValidationIssueResult(
            String processId, Object result, URI provideMoreParamsDialog, Set<ValidationIssue> violations
    ) {
        super(processId, result);
        this.provideMoreParamsDialog = provideMoreParamsDialog;
        this.violations = violations;
    }
}
