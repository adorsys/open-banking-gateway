package de.adorsys.opba.fintech.impl.service;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ContextInformation {
    private UUID xRequestID;
    public ContextInformation(UUID xRequestID) {
        if (xRequestID == null) {
            throw new RuntimeException("RequestID never must be null. Invalid call is rejected");
        }
        this.xRequestID = xRequestID;
    }


    // TODO has to be synchronized with backend, see technical ui /initial
    private final String fintechID = "MY-SUPER-FINTECH-ID";
    private final String serviceSessionPassword = "qwerty";
}
