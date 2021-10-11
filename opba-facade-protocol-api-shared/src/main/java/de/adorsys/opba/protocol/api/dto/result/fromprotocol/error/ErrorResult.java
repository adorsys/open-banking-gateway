package de.adorsys.opba.protocol.api.dto.result.fromprotocol.error;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result from protocol representing some (most probably unrecoverable) error has happened.
 */
@Data
@NoArgsConstructor
public class ErrorResult<T> implements Result<T> {

    /**
     * Human readable error message.
     */
    private String message;

    /**
     * Machine parseable error code.
     */
    private String code;

    private boolean canRedirectBackToFintech;

    public ErrorResult(String message) {
        this.message = message;
    }
}
