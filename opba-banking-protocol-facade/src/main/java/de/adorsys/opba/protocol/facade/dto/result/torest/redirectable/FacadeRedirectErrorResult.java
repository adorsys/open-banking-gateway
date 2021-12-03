package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_CODE;
import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_MESSAGE;

/**
 * The result that indicate error has happened and user should be redirected somewhere.
 * @param <T> Result body type
 * @param <C> Authorization state
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FacadeRedirectErrorResult<T, C extends AuthStateBody> extends FacadeResultRedirectable<T, C> {

    public static final ErrorFromProtocol ERROR_FROM_PROTOCOL = Mappers.getMapper(ErrorFromProtocol.class);

    private Map<String, String> headers = new HashMap<>();

    @Mapper
    public interface ErrorFromProtocol {

        void map(ErrorResult source, @MappingTarget FacadeRedirectErrorResult target);

        default FacadeRedirectErrorResult map(ErrorResult result) {
            FacadeRedirectErrorResult mapped = new FacadeRedirectErrorResult();
            map(result, mapped);
            mapped.getHeaders().put(X_ERROR_CODE, result.getCode());
            mapped.getHeaders().put(X_ERROR_MESSAGE, result.getMessage());
            return mapped;
        }
    }
}
