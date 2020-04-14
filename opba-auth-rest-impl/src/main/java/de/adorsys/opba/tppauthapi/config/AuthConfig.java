package de.adorsys.opba.tppauthapi.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import de.adorsys.opba.protocol.facade.config.auth.TppTokenProperties;
import de.adorsys.opba.tppauthapi.controller.TppAuthResponseCookieTemplate;
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
    TppAuthResponseCookieTemplate tppAuthResponseCookieBuilder(CookieProperties cookieProperties) {
        return new TppAuthResponseCookieTemplate(cookieProperties);
    }

    @Bean
    JWSHeader jwsHeaderBuilder(TppTokenProperties tppTokenProperties) {
        return new JWSHeader.Builder(JWSAlgorithm.parse(tppTokenProperties.getJwsAlgo())).build();
    }

    @Bean
    JWSSigner rsassaSigner(TppTokenProperties tppTokenProperties) {
        return new RSASSASigner(loadPrivateKey(tppTokenProperties));
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
}
