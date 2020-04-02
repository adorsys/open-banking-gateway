package de.adorsys.opba.tppauthapi.controller;

import de.adorsys.opba.tppauthapi.resource.generated.UserRegisterApi;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserRegistrationController implements UserRegisterApi {
    @Override
    public CompletableFuture<ResponseEntity<Void>> registration(String authorization, UUID xRequestID, String username, String password) {
        return null;
    }
}
