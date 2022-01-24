package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.common.SessionStatus;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public interface AuthorizationStatusBody extends ResultBody {

    SessionStatus getStatus();
    Map<UUID, DetailedSessionStatus> getDetailedStatus();
    String getExternalLinkForPsu();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getUpdatedAt();

    void setStatus(SessionStatus status);
    void setDetailedStatus(Map<UUID, DetailedSessionStatus> detailedStatus);
    void setExternalLinkForPsu(String externalLinkForPsu);
    void setCreatedAt(OffsetDateTime createdAt);
    void setUpdatedAt(OffsetDateTime updatedAt);
}
