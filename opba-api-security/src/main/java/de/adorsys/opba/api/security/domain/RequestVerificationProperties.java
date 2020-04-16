package de.adorsys.opba.api.security.domain;

import lombok.Data;

import java.time.Duration;

@Data
public class RequestVerificationProperties {
    private Duration requestTimeLimit;
}
