package de.adorsys.opba.protocol.facade.dto.result.torest.staticres;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * Generic success result used for i.e. ListAccounts response.
 * @param <T> Response body
 */
@Data
public class FacadeSuccessResult<T> implements FacadeResult<T> {

    public static final SuccessFromProtocol FROM_PROTOCOL = Mappers.getMapper(SuccessFromProtocol.class);

    private UUID xRequestId;
    private String serviceSessionId;
    private T body;

    @Mapper
    public interface SuccessFromProtocol {
        FacadeSuccessResult map(SuccessResult result);
    }
}
