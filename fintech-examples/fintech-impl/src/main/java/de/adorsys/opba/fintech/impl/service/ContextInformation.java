//package de.adorsys.opba.fintech.impl.service;
//
//import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.UUID;
//
//@Getter
//@Slf4j
//public class ContextInformation {
//    private UUID xRequestID;
//
//    @Autowired
//    private RestRequestContext restRequestContext;
//
//    public ContextInformation() {
//        log.info("ContextInformation Construction {}", restRequestContext == null ? "null" : "VALID");
//        this.xRequestID = UUID.fromString(restRequestContext.getRequestId());
//        if (this.xRequestID == null) {
//            throw new RuntimeException("RequestID never must be null. Invalid call is rejected");
//        }
//    }
//
//    private final String fintechID = "${tpp.fintechID}";
//    private final String serviceSessionPassword = "${tpp.serviceSessionPassword}";
//}
