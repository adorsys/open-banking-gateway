package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.xs2a.adapter.api.RequestParams;
import lombok.Data;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Object that represents request Parameters that are necessary to call ASPSP API for consent initiation.
 */
@Data
public class ConsentInitiateParameters {

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromAisCtx extends DtoMapper<Xs2aAisContext, ConsentInitiateParameters> {
        ConsentInitiateParameters map(Xs2aContext ctx);
    }

    public RequestParams toParameters() {
        return RequestParams.builder().build();
    }
}
