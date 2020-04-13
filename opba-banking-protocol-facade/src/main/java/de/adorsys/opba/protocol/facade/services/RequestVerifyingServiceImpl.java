package de.adorsys.opba.protocol.facade.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

// TODO Stub implementation until Libaraty finally decided
@Service
public class RequestVerifyingServiceImpl implements RequestVerifyingService {
    @Override
    public String verify(String value, byte[] publicKey) {
        return UUID.randomUUID().toString();
    }
}
