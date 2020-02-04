package de.adorsys.opba.protocol.facade.dto.result.torest;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ErrorResult;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Data
public class FacadeErrorResult<T> implements FacadeResult<T> {

    public static final ErrorFromProtocol FROM_PROTOCOL = Mappers.getMapper(ErrorFromProtocol.class);

    private String message;
    private String code;

    @Mapper
    public interface ErrorFromProtocol {
        FacadeErrorResult map(ErrorResult result);
    }
}
