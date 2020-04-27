package de.adorsys.opba.protocol.facade.dto.result.torest.staticres;

import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Should be used for fatal errors.
 */
@Data
@RequiredArgsConstructor
public class FacadeErrorResult<T> implements FacadeResult<T> {

    private final T body;
    private final UUID xRequestId;
    private final String serviceSessionId;
}
