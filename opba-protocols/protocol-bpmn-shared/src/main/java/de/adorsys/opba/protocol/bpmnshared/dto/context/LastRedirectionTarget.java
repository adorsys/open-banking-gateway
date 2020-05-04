package de.adorsys.opba.protocol.bpmnshared.dto.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.UsesRequestScoped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Object to store where the protocol was requesting user to redirect to.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
// TODO - decide do we need to encrypt these
public class LastRedirectionTarget implements UsesRequestScoped, RequestScoped {

    private String redirectTo;
    private String redirectToUiScreen;

    /**
     * Request scoped services provider for sensitive data.
     */
    @Delegate
    @JsonIgnore
    private RequestScoped requestScoped;
}
