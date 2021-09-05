package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.common.SessionStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * The response from AIS authorization status to request
 * {@link de.adorsys.opba.protocol.api.dto.request.accounts.AisAuthorizationStatusRequest}.
 */
@Data
public class AisAuthorizationStatusBody implements ResultBody {

    private SessionStatus status;
    private Map<UUID, DetailedSessionStatus> detailedStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
