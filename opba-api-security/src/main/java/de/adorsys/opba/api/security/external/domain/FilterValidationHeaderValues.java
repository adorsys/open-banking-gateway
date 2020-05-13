package de.adorsys.opba.api.security.external.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilterValidationHeaderValues {
    private String fintechId;
    private String xRequestId;
    private String requestTimeStamp;
    private String operationType;
    private String xRequestSignature;
    private String requestPath;
}
