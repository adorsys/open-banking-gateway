package de.adorsys.opba.fintech.impl.exceptions;

import lombok.Getter;

@Getter
public class ConsentException extends RuntimeException {
    public ConsentException(int httpResponseCode, String xErrorCode) {
        super("XERROR CODE:" + xErrorCode);
        this.httpResponseCode = httpResponseCode;
        this.xErrorCode = xErrorCode;
    }
    private final int httpResponseCode;
    private final String xErrorCode;
}
