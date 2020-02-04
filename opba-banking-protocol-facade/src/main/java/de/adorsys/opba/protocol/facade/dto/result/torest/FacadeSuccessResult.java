package de.adorsys.opba.protocol.facade.dto.result.torest;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.SuccessResult;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Data
public class FacadeSuccessResult<T> implements FacadeResult<T> {

    public static final SuccessFromProtocol FROM_PROTOCOL = Mappers.getMapper(SuccessFromProtocol.class);

    private T body;

    @Mapper
    public interface SuccessFromProtocol {
        FacadeSuccessResult map(SuccessResult result);
    }
}
