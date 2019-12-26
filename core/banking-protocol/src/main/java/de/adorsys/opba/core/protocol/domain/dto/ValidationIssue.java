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

    private String beanName;
    private String propertyPath;
    private String message;
    private String code;
}
