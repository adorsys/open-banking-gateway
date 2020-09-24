package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReturnableProcessErrorResult<T> implements Result<T> {
    /** the error code should be returned to the caller, it may be used in the
     * rest layer to use a specific http response code
     */
    private final String errorCodeString;
}
