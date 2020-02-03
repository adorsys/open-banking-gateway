package de.adorsys.opba.protocol.api.dto.request.authentication;

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

// TODO Validation
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequest implements FacadeServiceableGetter {

    private FacadeServiceableRequest facadeServiceable;

    @Builder.Default
    private Map<ExtraAuthRequestParam, Object> extras = new EnumMap<>(ExtraAuthRequestParam.class);
}
