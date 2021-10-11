package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

/**
 * The response from AIS authorization status to request
 * {@link de.adorsys.opba.protocol.api.dto.request.accounts.UpdateExternalAisSessionRequest}.
 */
@Data
public class UpdateExternalAisSessionBody implements ResultBody {

    private String status;
}
