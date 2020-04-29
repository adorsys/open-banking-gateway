package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment;

import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.InitiateHeaders;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Object that represents request Headers that are necessary to call ASPSP API for consent initiation.
 */
public class PaymentInitiateHeaders extends InitiateHeaders {
    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromPisCtx extends DtoMapper<Xs2aPisContext, PaymentInitiateHeaders> {
        PaymentInitiateHeaders map(Xs2aPisContext ctx);
    }
}
