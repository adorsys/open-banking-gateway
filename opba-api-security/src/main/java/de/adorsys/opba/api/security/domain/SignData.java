package de.adorsys.opba.api.security.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class SignData implements SignatureClaim {
    private static final String DELIMITER = "_#_";
    private UUID xRequestId;
    private OffsetDateTime requestDateTime;

    public SignData(UUID xRequestId, OffsetDateTime requestDateTime) {
        this.xRequestId = xRequestId;
        this.requestDateTime = requestDateTime;
    }

    @Override
    public String getClaimsAsString() {
        return new StringBuilder().append(xRequestId)
                       .append(DELIMITER)
                       .append(requestDateTime)
                       .toString();
    }
}
