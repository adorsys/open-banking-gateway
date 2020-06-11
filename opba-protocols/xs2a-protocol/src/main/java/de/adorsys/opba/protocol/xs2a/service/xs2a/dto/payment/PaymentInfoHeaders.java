package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.WithBasicInfo;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import org.mapstruct.Mapper;

import java.util.Map;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

public class PaymentInfoHeaders extends WithBasicInfo {

    public RequestHeaders toHeaders() {
        Map<String, String> headers = super.asMap();
        return RequestHeaders.fromMap(headers);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromPisCtx extends DtoMapper<Xs2aPisContext, PaymentInfoHeaders> {
        PaymentInfoHeaders map(Xs2aPisContext ctx);
    }
}
