package de.adorsys.opba.fintech.impl.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConsentException extends RuntimeException {
    private final int httpResponseCode;
    private final String xErrorCode;
}
