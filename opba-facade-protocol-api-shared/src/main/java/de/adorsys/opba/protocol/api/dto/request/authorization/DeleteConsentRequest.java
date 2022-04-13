package de.adorsys.opba.protocol.api.dto.request.authorization;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.UpdateMetadataDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The request to delete consent and unerlying connection.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteConsentRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * If true FinAPI user will be deleted when deleting consent.
     */
    private boolean deleteAll;
    /**
     * Additional protocol specific properties
     */
    private UpdateMetadataDetails details;
}
