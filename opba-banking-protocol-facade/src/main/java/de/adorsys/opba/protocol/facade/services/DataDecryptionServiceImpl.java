package de.adorsys.opba.protocol.facade.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

// TODO Stub implementation until Libaraty finally decided
@Service
public class DataDecryptionServiceImpl implements DataDecryptionService {
    @Override
    public String decrypt(String value, byte[] publicKey) {
        return UUID.randomUUID().toString();
    }
}
