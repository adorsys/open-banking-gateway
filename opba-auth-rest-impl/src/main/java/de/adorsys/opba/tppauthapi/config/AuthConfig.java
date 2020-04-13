package de.adorsys.opba.tppauthapi.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import de.adorsys.opba.tppauthapi.controller.TppAuthResponseCookie;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class AuthConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    TppAuthResponseCookie.TppAuthResponseCookieBuilder tppAuthResponseCookieBuilder(CookieProperties cookieProperties) {
        return TppAuthResponseCookie.builder().cookieProperties(cookieProperties);
    }

    @Bean
    JWSHeader jwsHeaderBuilder(TppProperties tppProperties) {
        return new JWSHeader.Builder(JWSAlgorithm.parse(tppProperties.getJwsAlgo())).build();
    }

    @Bean
    JWSSigner rsassaSigner(TppProperties tppProperties) {
        return new RSASSASigner(loadPrivateKey(tppProperties));
    }

    /**
     * See {@code de.adorsys.opba.tppauthapi.TokenSignVerifyTest#generateNewTppKeyPair()} for details of how to
     * generate the encoded key.
     */
    @SneakyThrows
    private PrivateKey loadPrivateKey(TppProperties tppProperties) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(tppProperties.getPrivateKey());
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(tppProperties.getSignAlgo());
        return kf.generatePrivate(ks);
    }
}
