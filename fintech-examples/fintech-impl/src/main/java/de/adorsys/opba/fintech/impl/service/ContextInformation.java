package de.adorsys.opba.fintech.impl.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ContextInformation {
    private final UUID xRequestID;
    // TODO has to be synchronized with backend, see technical ui /initial
    private final String fintechID = "MY-SUPER-FINTECH-ID";
    private final String serviceSessionPassword = "qwerty";
}
