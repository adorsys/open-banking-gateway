package de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Protocol result that represents operation was OK and contains result as a body (i.e. PSU account list).
 */
@Data
@AllArgsConstructor
public class SuccessResult<T> implements Result<T> {

    /**
     * Operation result (i.e. account list).
     */
    private T body;
}
