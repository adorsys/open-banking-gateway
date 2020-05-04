package de.adorsys.opba.api.security.internal.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Validated
@Configuration
public class TppTokenConfig {

    @Bean
    JWSHeader jwsHeaderBuilder(TppTokenProperties tppTokenProperties) {
        return new JWSHeader.Builder(JWSAlgorithm.parse(tppTokenProperties.getJwsAlgo())).build();
    }

    @Bean
    JWSSigner rsassaSigner(TppTokenProperties tppTokenProperties) {
        return new RSASSASigner(loadPrivateKey(tppTokenProperties));
    }

    @Bean
    JWSVerifier rsassaVerifier(TppTokenProperties tppTokenProperties) {
        return new RSASSAVerifier(loadPublicKey(tppTokenProperties));
    }

    /**
     * See {@code de.adorsys.opba.tppauthapi.TokenSignVerifyTest#generateNewTppKeyPair()} for details of how to
     * generate the encoded key.
     */
    @SneakyThrows
    private PrivateKey loadPrivateKey(TppTokenProperties tppTokenProperties) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(tppTokenProperties.getPrivateKey());
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppTokenProperties.getSignAlgo());
        return kf.generatePrivate(ks);
    }

    /**
     * See {@code de.adorsys.opba.tppauthapi.TokenSignVerifyTest#generateNewTppKeyPair()} for details of how to
     * generate the encoded key.
     */
    @SneakyThrows
    private RSAPublicKey loadPublicKey(TppTokenProperties tppTokenProperties) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(tppTokenProperties.getPublicKey());
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppTokenProperties.getSignAlgo());
        return (RSAPublicKey) kf.generatePublic(ks);
    }
}
