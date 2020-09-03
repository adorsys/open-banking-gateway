package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ResponseTokenMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aOauth2Headers extends Xs2aStandardHeaders {

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = ResponseTokenMapper.class)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aOauth2Headers> {

        Xs2aOauth2Headers map(Xs2aContext ctx);
    }
}
