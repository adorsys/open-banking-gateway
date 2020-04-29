package de.adorsys.opba.api.security.service;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.service.TokenBasedAuthService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = TokenSignVerifyTest.TestConfig.class)
public class TokenSignVerifyTest {

    @Autowired
    private TokenBasedAuthService psuAuthService;

    @Autowired
    private JWSVerifier verifier;

    @Test
    @SneakyThrows
    void signVerifyTest() {
        String message = "some data";

        String token = psuAuthService.generateToken(message);

        JWSObject jwsObject = JWSObject.parse(token);
        assertThat(jwsObject.verify(verifier)).isTrue();
        assertThat(jwsObject.getPayload().toJSONObject().get("sub")).isEqualTo(message);
    }

    @Test
    @SneakyThrows
    @Disabled
    void generateNewTppKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.genKeyPair();

        String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        log.info("Private key:" + privateKeyString);
        log.info("Public key:" + publicKeyString);

    }

    @Configuration
    @EnableTokenBasedApiSecurity
    public static class TestConfig {
    }
}
