package de.adorsys.opba.core.protocol.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationIssue {

    private String uiCode;
    private String ctxCode;
    private String message;

    @Override
    public String toString() {
        return "{"
                + "\"uiCode\":\"" + uiCode + "\""
                + ", \"ctxCode\":\"" + ctxCode + "\""
                + ", \"message\":\"" + message + "\""
                + "}";
    }
}
