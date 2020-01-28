package de.adorsys.opba.fintech.impl.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ContextInformation {
    private final UUID xRequestID;
    private final String fintechID = "my-fintech-id";
}
