package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Getter
public class ContextInformation {
    private UUID xRequestID;

    @Autowired
    private RestRequestContext restRequestContext;

    @PostConstruct
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void postConstruct() {
        this.xRequestID = UUID.fromString(restRequestContext.getRequestId());
        if (this.xRequestID == null) {
            throw new RuntimeException("RequestID never must be null. Invalid call is rejected");
        }
    }

    public ContextInformation() {
    }

    private final String fintechID = "${tpp.fintechID}";
    private final String serviceSessionPassword = "${tpp.serviceSessionPassword}";
}
