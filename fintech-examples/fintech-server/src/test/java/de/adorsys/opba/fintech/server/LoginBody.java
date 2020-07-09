package de.adorsys.opba.fintech.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class LoginBody {
    String username;
    String password;
}
