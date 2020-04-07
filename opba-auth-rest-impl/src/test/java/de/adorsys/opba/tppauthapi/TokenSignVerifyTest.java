package de.adorsys.opba.tppauthapi;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import de.adorsys.opba.tppauthapi.config.CookieProperties;
import de.adorsys.opba.tppauthapi.config.TppProperties;
import de.adorsys.opba.tppauthapi.controller.PsuAuthController;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TppProperties.class, CookieProperties.class})
@AutoConfigureMockMvc
public class TokenSignVerifyTest {

    @Autowired
    private TppProperties tppProperties;

    @Autowired
    private CookieProperties cookieProperties;

    @Test
    @SneakyThrows
    void signVerifyTest() {
        String message = "some data";

        PsuAuthController psuAuthController = new PsuAuthController(null, cookieProperties, tppProperties);
        String token = psuAuthController.generateToken(message);

        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) psuAuthController.loadPublicKey());
        JWSObject jwsObject = JWSObject.parse(token);
        assertTrue(jwsObject.verify(verifier));
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
}
