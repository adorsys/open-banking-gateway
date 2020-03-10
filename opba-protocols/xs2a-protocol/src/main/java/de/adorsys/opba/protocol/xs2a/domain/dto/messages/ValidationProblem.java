package de.adorsys.opba.protocol.xs2a.domain.dto.messages;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ValidationProblem extends InternalProcessResult {

    @NonNull
    private URI provideMoreParamsDialog;

    @NonNull
    @Builder.Default
    private Set<ValidationIssue> issues = new HashSet<>();

    @Builder
    public ValidationProblem(String processId, String executionId, Object result, URI provideMoreParamsDialog,
                             Set<ValidationIssue> issues) {
        super(processId, executionId, result);
        this.provideMoreParamsDialog = provideMoreParamsDialog;
        this.issues = issues;
    }
}
