package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update authorization result object. Returned in response to user provided i.e. PSU ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAuthBody implements ResultBody {

    /**
     * Current authorization status (if available, used for Decoupled)
     */
    private String scaStatus;

    /**
     * Current SCA authentication type chosen by ASPSP (Decoupled).
     */
    private String scaAuthenticationType;

    /**
     * Message indicating action to take according the SCA (Decoupled)
     */
    private String scaExplanation;
}
