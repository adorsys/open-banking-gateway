package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_CODE;
import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_MESSAGE;

@Data
public class FacadeRuntimeErrorResult<T> implements FacadeResult<T> {

    public static final ErrorFromProtocol ERROR_FROM_PROTOCOL = Mappers.getMapper(ErrorFromProtocol.class);

    private UUID xRequestId;
    private String serviceSessionId;

    private Map<String, String> headers = new HashMap<>();

    @Mapper
    public interface ErrorFromProtocol {

        void map(ErrorResult source, @MappingTarget FacadeRuntimeErrorResult target);

        default FacadeRuntimeErrorResult map(ErrorResult result) {
            FacadeRuntimeErrorResult mapped = new FacadeRuntimeErrorResult();
            map(result, mapped);
            mapped.getHeaders().put(X_ERROR_CODE, result.getCode());
            mapped.getHeaders().put(X_ERROR_MESSAGE, result.getMessage());
            return mapped;
        }
    }
}
