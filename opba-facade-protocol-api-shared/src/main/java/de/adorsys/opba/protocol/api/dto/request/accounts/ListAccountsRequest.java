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

/**
 * The request by FinTech to list PSUs' accounts.
 */
// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountsRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * Additional (protocol-customary) request parameters.
     */
    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
