package de.adorsys.opba.protocol.xs2a.domain.dto;

import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.TargetObject;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode(of = {"ctxCode", "target"})
public class ValidationIssue {

    @NonNull
    private final String uiCode;

    @NonNull
    private final String ctxCode;

    @NonNull
    private final String message;

    @NonNull
    private final TargetObject target;

    @Override
    public String toString() {
        return "{"
                + "\"uiCode\":\"" + uiCode + "\""
                + ", \"ctxCode\":\"" + ctxCode + "\""
                + ", \"message\":\"" + message + "\""
                + ", \"target\":\"" + target + "\""
                + "}";
    }
}
