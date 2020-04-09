package de.adorsys.opba.tppauthapi;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import de.adorsys.opba.tppauthapi.config.TppProperties;
import de.adorsys.opba.tppauthapi.service.PsuAuthService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = TokenSignVerifyTest.TestConfig.class)
public class TokenSignVerifyTest {

    @Autowired
    private TppProperties tppProperties;
    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    private PsuRepository psuRepository;
    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    private PsuSecureStorage psuSecureStorage;
    @Autowired
    PsuAuthService psuAuthService;


    @Test
    @SneakyThrows
    void signVerifyTest() {
        String message = "some data";

        String token = psuAuthService.generateToken(message);

        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) loadPublicKey());
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

    @SneakyThrows
    private PublicKey loadPublicKey() {
        byte[] publicKeyBytes = Base64.getDecoder().decode(tppProperties.getPublicKey());
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppProperties.getSignAlgo());
        return kf.generatePublic(ks);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.tppauthapi")
    @EnableConfigurationProperties
    public static class TestConfig {
    }
}
