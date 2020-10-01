package de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * The request to handle returns from ASPSP - either OK or NOK from redirect consent authorization.
 */
// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FromAspspRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * Is it from ASPSP OK return (consent granted) or NOK (consent denied).
     */
    @NonNull
    private Boolean isOk;

    /**
     * OAuth2 code to be exchanged to token. Used only for redirects' back in OAuth2 authentication/authorization.
     */
    private String code;
}
