package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SensitiveAuthLevel {

    AUTHENTICATED(false),
    AUTHORIZED(true);

    private final boolean needsTan;

    public String needsTanJaNein() {
        return needsTan ? "J" : "N";
    }
}
