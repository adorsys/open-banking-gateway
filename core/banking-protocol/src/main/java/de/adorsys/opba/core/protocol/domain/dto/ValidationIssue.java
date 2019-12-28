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

    private String code;
    private String message;

    /**
     * @return Json-alike entry
     */
    @Override
    public String toString() {
        return "{" + "\"code\":\"" + code + "\", \"message\":\"" + message + "\"}";
    }
}
