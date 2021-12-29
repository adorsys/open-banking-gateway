package de.adorsys.opba.protocol.api.dto.request.accounts;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The request to get AIS authorization status
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AisAuthorizationStatusRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * External session ID for i.e. FinAPI (Task ID for update connections).
     */
    private String externalSessionId;
}
