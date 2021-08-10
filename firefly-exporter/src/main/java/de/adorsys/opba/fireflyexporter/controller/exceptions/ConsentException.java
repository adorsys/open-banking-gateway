package de.adorsys.opba.fireflyexporter.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Consent limits seem to be exhausted")
public class ConsentException extends RuntimeException {
}
