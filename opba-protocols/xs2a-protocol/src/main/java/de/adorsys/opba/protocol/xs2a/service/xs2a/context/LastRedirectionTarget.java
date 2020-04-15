package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.UsesRequestScoped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

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
