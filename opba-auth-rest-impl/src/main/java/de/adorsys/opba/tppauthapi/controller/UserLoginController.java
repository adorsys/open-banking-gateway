package de.adorsys.opba.tppauthapi.controller;

import de.adorsys.opba.tppauthapi.resource.generated.UserLoginApi;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserLoginController implements UserLoginApi {
    @Override
    public CompletableFuture<ResponseEntity<Void>> _login(String authorization, UUID xRequestID, @NotNull @Valid String username, @NotNull @Valid String password) {
        return null;
    }
}
