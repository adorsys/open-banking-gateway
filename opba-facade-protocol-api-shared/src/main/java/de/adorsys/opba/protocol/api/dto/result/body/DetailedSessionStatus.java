package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.common.SessionStatus;
import lombok.Data;

@Data
public class DetailedSessionStatus {

    private SessionStatus status;
    private String externalStatus;
}
