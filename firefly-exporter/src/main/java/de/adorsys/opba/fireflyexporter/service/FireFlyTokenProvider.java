package de.adorsys.opba.fireflyexporter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class FireFlyTokenProvider {

    private final AtomicReference<String> tokenValue = new AtomicReference<>();

    public String getToken() {
        return tokenValue.get();
    }

    public void setToken(String token) {
        tokenValue.set(token);
    }
}
