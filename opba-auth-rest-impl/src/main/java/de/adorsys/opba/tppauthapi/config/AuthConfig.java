package de.adorsys.opba.tppauthapi.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class AuthConfig {

    @Bean
    JWSHeader jwsHeaderBuilder(TppProperties tppProperties) {
        return new JWSHeader.Builder(JWSAlgorithm.parse(tppProperties.getJwsAlgo())).build();
    }

    @Bean
    RSASSASigner rsassaSigner(TppProperties tppProperties) {
        return new RSASSASigner(loadPrivateKey(tppProperties));
    }

    @SneakyThrows
    private PrivateKey loadPrivateKey(TppProperties tppProperties) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(tppProperties.getPrivateKey());
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppProperties.getSignAlgo());
        return kf.generatePrivate(ks);
    }
}
