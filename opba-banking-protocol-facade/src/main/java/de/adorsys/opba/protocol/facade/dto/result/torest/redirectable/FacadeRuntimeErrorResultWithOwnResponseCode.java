package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.body.ReturnableProcessErrorResult;
import de.adorsys.opba.protocol.api.errors.ProcessErrorStrings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_CODE;

@Slf4j
@Getter
@Setter
public class FacadeRuntimeErrorResultWithOwnResponseCode<T> extends FacadeRuntimeErrorResult<T> {
    private static final int DEFAULT_RESPONSE_CODE = 500;

    private static final int CONSENT_UNKNOWN_RESPONSE_CODE = 410;
    private static final int CONSENT_USAGE_LIMIT_EXCEEDED_HTTP_RESPONSE_CODE = 429;

    private int responseCode = DEFAULT_RESPONSE_CODE;
    public static final FacadeRuntimeErrorResultWithOwnResponseCode.ErrorWithBodyFromProtocol ERROR_FROM_PROTOCOL =
        Mappers.getMapper(FacadeRuntimeErrorResultWithOwnResponseCode.ErrorWithBodyFromProtocol.class);

    @Mapper
    public interface ErrorWithBodyFromProtocol {

        void map(ReturnableProcessErrorResult source, @MappingTarget FacadeRuntimeErrorResultWithOwnResponseCode target);

        default FacadeRuntimeErrorResultWithOwnResponseCode map(ReturnableProcessErrorResult result) {
            FacadeRuntimeErrorResultWithOwnResponseCode mapped = new FacadeRuntimeErrorResultWithOwnResponseCode<>();
            map(result, mapped);
            mapped.getHeaders().put(X_ERROR_CODE, result.getErrorCodeString());
            switch (result.getErrorCodeString()) {
                case ProcessErrorStrings.CONSENT_ACCESS_EXCEEDED_LIMIT:
                    mapped.setResponseCode(CONSENT_USAGE_LIMIT_EXCEEDED_HTTP_RESPONSE_CODE);
                    break;
                case ProcessErrorStrings.CONSENT_UNKNOWN:
                    mapped.setResponseCode(CONSENT_UNKNOWN_RESPONSE_CODE);
                    break;
                default:
                    mapped.setResponseCode(DEFAULT_RESPONSE_CODE);
                    log.error("did not find specific error for {} to map to specific response code.", result.getErrorCodeString());
            }
            return mapped;
        }
    }
}
