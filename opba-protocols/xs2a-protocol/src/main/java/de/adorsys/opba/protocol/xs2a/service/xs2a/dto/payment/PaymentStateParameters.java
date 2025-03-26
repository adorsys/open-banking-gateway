package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment;

import de.adorsys.opba.protocol.api.dto.payment.PaymentType;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.xs2a.adapter.api.RequestParams;
import lombok.Data;
import org.mapstruct.Mapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
public class PaymentStateParameters {

    /**
     * Payment ID that uniquely identifies the payment within ASPSP. Highly sensitive field.
     */
    @NotBlank
    private String paymentId;

    /**
     * Payment service is provided by ASPSP.
     */
    @NotNull
    private PaymentType paymentType;

    /**
     * Payment product is provided by ASPSP.
     */
    @NotBlank
    private String paymentProduct;

    public RequestParams toParameters() {
        return RequestParams.builder().build();
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aPisContext, PaymentStateParameters> {
        PaymentStateParameters map(Xs2aPisContext ctx);
    }
}
