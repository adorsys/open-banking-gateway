/*
package de.adorsys.opba.fintech.impl.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class TestJwt {
    private static final String SECRET_KEY = "Vanya";

    @Test
    void registerRedirectUrlForSession() throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        String digitalSign = createJWT("111111", "roman", "mail", 121212L);

        boolean result = verifySign(digitalSign);

        assertThat(1).isEqualTo(1);

    }


    public static String createJWT(String id, String issuer, String subject, long ttlMillis) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] pkcs8EncodedBytes =  Base64.getEncoder().encode(getPrivetKey().replaceAll(" ", "").getBytes());


        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(getPublicKey()));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);


        try {
            Algorithm algorithm = Algorithm.RSA256(pubKey, privKey);
            String token = JWT.create()
                                   .withIssuer("auth0")
                                   .withClaim("name", "Vanya")
                                   .sign(algorithm);
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }

        return null;
    }


    boolean verifySign(String signature) {


        return false;
    }


    private static String getPrivetKey() {
        return "MIICWwIBAAKBgQCYuT5wLhUwLFgcvyeZQRqhMfo9O48aNj8+tNWtu5ThpoEZiJtV R970C/N1NByikNjzOhBaEuucfttW4mJ6ofp9ZgruzegzJiMYnDcGuOrOzyUGTkoG Ik9BdgNW+tM6ApLqyy9seOk6eAHhdu8EATfpkBNt6KxToMwiD4oBQcx7swIDAQAB AoGAXwWi25lQhZCRohEtSiU/tFusHr0X5G8sGo/ZAydbEqrOWFyuiPkWtzFYYOvz hRIqaesOkXyEK/Kh9gUU0MhHWRBkzzIRiYDKjh+TN4gRVeJDUDiJiYiEn6W/ZXTa +nicTHkluNV7dEYDeE8qXIX7SX3sMBNdtHVIhF3UDFQLZMECQQD+ZDqx0Q3DuOyx uXFZ79UqQLO9TfzKkmGoBObdkxo58TPO+BciLujoPmUNEeYqMr2048wE6wQhYsRF peMe1YTFAkEAmbBzLM+aMdIyFJI7WJilcYDpEYA3o0noIJIhopjGz8V+ZaA+A5PV sit2q9frj9oH4xYHpkr65iRYk1fsqew2FwJAJLJ0vR35LFjK3EByF5U/XN8EjrRn WRmQuNosK56C1AT1gk/LloTJ2GbX0PDaERBMyYFq9vKoH+DNi3aIsvP+OQJAGwIc K2rMQSccL+tGzJn+sQSjcLTkkiiBx3+Gs6k/fvHI9ZkEbOKE8kubDjXiqqP5MNoF PB7/GoSWoEYaS/47vwJAXdh655/L9rVTIxX1UpI31raIELpXJo96yxAFDT1T3uZg C/86Ox2qPKWCMJDmsymnK6E0MlbfWGnFQIOSJzln1A==";
    }

    private static String getPublicKey() {
        return "-----BEGIN PUBLIC KEY-----\n" +
                       "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYuT5wLhUwLFgcvyeZQRqhMfo9\n" +
                       "O48aNj8+tNWtu5ThpoEZiJtVR970C/N1NByikNjzOhBaEuucfttW4mJ6ofp9Zgru\n" +
                       "zegzJiMYnDcGuOrOzyUGTkoGIk9BdgNW+tM6ApLqyy9seOk6eAHhdu8EATfpkBNt\n" +
                       "6KxToMwiD4oBQcx7swIDAQAB\n" +
                       "-----END PUBLIC KEY-----";
    }
}
*/
