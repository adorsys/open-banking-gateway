package de.adorsys.opba.tppauthapi.controller;

import de.adorsys.opba.tppauthapi.resource.generated.PsuAuthApi;
import de.adorsys.opba.tppauthapi.service.PsuAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class PsuAuthController implements PsuAuthApi {

    private final PsuAuthService psuAuthService;

    @Override
    public CompletableFuture login(String authorization, UUID xRequestID, String psuId, String password) {
        return null;
    }

    @Override
    public CompletableFuture registration(String authorization, UUID xRequestID, String psuId, String password) {
        return null;
    }
}
