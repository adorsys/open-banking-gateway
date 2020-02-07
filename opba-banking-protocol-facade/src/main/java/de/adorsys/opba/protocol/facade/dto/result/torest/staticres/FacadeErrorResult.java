package de.adorsys.opba.protocol.facade.dto.result.torest.staticres;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Data
public class FacadeErrorResult<T> implements FacadeResult<T> {

    public static final ErrorFromProtocol FROM_PROTOCOL = Mappers.getMapper(ErrorFromProtocol.class);

    private UUID xRequestId;
    private String serviceSessionId;
    private String message;
    private String code;

    @Mapper
    public interface ErrorFromProtocol {
        FacadeErrorResult map(ErrorResult result);
    }
}
