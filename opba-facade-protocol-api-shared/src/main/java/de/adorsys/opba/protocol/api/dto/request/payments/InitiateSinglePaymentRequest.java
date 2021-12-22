package de.adorsys.opba.protocol.api.dto.request.payments;

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
 * The request by FinTech to initiate payment
 */
// TODO Validation, Immutability
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateSinglePaymentRequest implements FacadeServiceableGetter {

    /**
     * The request representation that is being serviced by facade.
     */
    private FacadeServiceableRequest facadeServiceable;

    /**
     * Payment access scope object.
     */
    private SinglePaymentBody singlePayment;

    /**
     * Additional (protocol-customary) request parameters.
     */
    @Builder.Default
    private Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
}
