package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ResponseTokenMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateV139Headers;
import lombok.Data;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Object that represents request Headers that are necessary to call ASPSP API Version 1.3.9 for consent initiation.
 */
@Data
@SuppressWarnings("PMD")
public class PaymentInitiateV139Headers extends ConsentInitiateV139Headers {

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = ResponseTokenMapper.class)
    public interface FromPisCtx extends DtoMapper<Xs2aPisContext, PaymentInitiateV139Headers> {
        PaymentInitiateV139Headers map(Xs2aPisContext ctx);
    }


}
