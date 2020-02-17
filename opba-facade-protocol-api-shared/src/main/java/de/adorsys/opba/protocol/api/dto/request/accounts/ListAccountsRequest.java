package de.adorsys.opba.protocol.api.dto.request.accounts;

import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountsRequest implements FacadeServiceableGetter {

    private FacadeServiceableRequest facadeServiceable;

    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
