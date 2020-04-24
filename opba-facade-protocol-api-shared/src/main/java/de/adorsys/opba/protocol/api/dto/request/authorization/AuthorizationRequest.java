package de.adorsys.opba.protocol.api.dto.request.authorization;

import de.adorsys.opba.protocol.api.dto.parameters.ExtraAuthRequestParam;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The request to update or get authorization state.
 */
// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * Account access scope object.
     */
    private AisConsent aisConsent;

    /**
     * SCA authentication data like challenge result (i.e. secret code from SMS 2FA)
     */
    private Map<String, String> scaAuthenticationData = new ConcurrentHashMap<>();

    /**
     * Additional (protocol-customary) request parameters.
     */
    @Builder.Default
    private Map<ExtraAuthRequestParam, Object> extras = new EnumMap<>(ExtraAuthRequestParam.class);
}
