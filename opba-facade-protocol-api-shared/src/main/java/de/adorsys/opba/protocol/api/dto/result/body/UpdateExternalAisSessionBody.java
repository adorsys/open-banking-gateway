package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.common.SessionStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * The response from AIS authorization status to request
 * {@link de.adorsys.opba.protocol.api.dto.request.accounts.UpdateExternalAisSessionRequest}.
 */
@Data
public class UpdateExternalAisSessionBody implements ResultBody {

    private String status;
}
