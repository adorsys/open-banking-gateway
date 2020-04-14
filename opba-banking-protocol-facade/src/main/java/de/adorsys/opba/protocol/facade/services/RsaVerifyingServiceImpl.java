package de.adorsys.opba.protocol.facade.services;

import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.rsa.RSAVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class RsaVerifyingServiceImpl implements RequestVerifyingService {
    private static final String CLAIM_NAME = "sign-data";

    @Override
    public String verify(String signature, String publicKey) {
        Verifier verifier = RSAVerifier.newVerifier(publicKey);

        JWT jwt = JWT.getDecoder().decode(signature, verifier);

        Optional<String> signData = Optional.ofNullable(jwt.getOtherClaims())
                                            .map(cl -> (String) cl.get(CLAIM_NAME));

        if (!signData.isPresent()) {
            log.warn("Signature verification error:  {}", signature);
            return null;
        }

        return signData.get();
    }
}
