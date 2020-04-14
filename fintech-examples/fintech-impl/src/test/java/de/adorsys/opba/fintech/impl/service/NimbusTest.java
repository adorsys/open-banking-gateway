/*
package de.adorsys.opba.fintech.impl.service;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.rsa.RSASigner;
import io.fusionauth.security.BCFIPSCryptoProvider;
import org.junit.jupiter.api.Test;

import java.util.Base64;


public class NimbusTest {

    @Test
    public void generate() {
        // Build an RSA signer using a SHA-256 hash

        //String pemPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" + getEncodedPrivateKey() + "\n-----END RSA PRIVATE KEY-----";


        Signer signer = RSASigner.newSHA256Signer(getEncodedPrivateKey());

// Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)


    }

*/
/*
    public static String generateJWTAssertion(String email, String privateKeyBase64,
                                              float expiryInSeconds) throws JoseException {

        final RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        rsaJsonWebKey.setPrivateKey();


        PrivateKey privateKey = rsaJsonWebKey.getRsaPrivateKey(privateKeyBase64);
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(email);
        claims.setAudience("https://api.metamind.io/v1/oauth2/token");
        claims.setExpirationTimeMinutesInTheFuture(expiryInSeconds / 60);
        claims.setIssuedAtToNow();

        // Generate the payload
        final JsonWebSignature jws = new JsonWebSignature();
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setPayload(claims.toJson());
        jws.setKeyIdHeaderValue(UUID.randomUUID().toString());

        // Sign using the private key
        jws.setKey(privateKey);
        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            return null;
        }
    }


    @Test
    public boolean verifyJwsCompactSerialization(String jwsCompactSerialization, byte[] secretKey) throws JoseException {

        String encodedPublicKey = getEncodedPublicKey();

        Key key = new AesKey(Base64.getDecoder().decode(encodedPublicKey));

        JsonWebSignature jws = new JsonWebSignature();

        jws.setCompactSerialization(jwsCompactSerialization);
        jws.setKey(key);

        boolean signatureVerified = jws.verifySignature();

        return signatureVerified;
    }

    private static PrivateKey getPrivateKey(String privateKeyBase64) throws IOException {
        String privKeyPEM = privateKeyBase64.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
        privKeyPEM = privKeyPEM.replace("\n-----END RSA PRIVATE KEY-----", "");

        // Base64 decode the data
        byte[] encoded = Base64.getDecoder().decode(privKeyPEM);

        try {
            DerInputStream derReader = new DerInputStream(encoded);
            DerValue[] seq = derReader.getSequence(0);

            if (seq.length < 9) {
                throw new GeneralSecurityException("Could not read private key");
            }

            // skip version seq[0];
            BigInteger modulus = seq[1].getBigInteger();
            BigInteger publicExp = seq[2].getBigInteger();
            BigInteger privateExp = seq[3].getBigInteger();
            BigInteger primeP = seq[4].getBigInteger();
            BigInteger primeQ = seq[5].getBigInteger();
            BigInteger expP = seq[6].getBigInteger();
            BigInteger expQ = seq[7].getBigInteger();
            BigInteger crtCoeff = seq[8].getBigInteger();

            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp,
                                                                    primeP, primeQ, expP, expQ, crtCoeff);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(keySpec);
        } catch (IOException | GeneralSecurityException e) {
            Throwables.propagate(e);
        }
        return null;
    }


    @Test
    public void test1() throws JOSEException, ParseException {
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                                .keyID(getEncodedPrivateKey())
                                .generate();

        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        JWSSigner signer = new RSASSASigner(rsaJWK);


// Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                new Payload("In RSA we trust!"));

// Compute the RSA signature
        jwsObject.sign(signer);

// To serialize to compact form, produces something like
// eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
// mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
// maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
// -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
        String s = jwsObject.serialize();

// To parse the JWS and verify it, e.g. on client-side
        jwsObject = JWSObject.parse(s);

        JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

    }*//*


    String getEncodedPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmn9/0kqSYqUmYcohClzkBMXLGoIXB5KPFRtxO2JIbpQplWbk7LYNAO/1VVke+4UjhyyfHm2lUFF8/JxJYlTbVeuM+p3GibdlnOzH5XxPD+I/K5t3MXQ+6Lnxq81QYOthU9borTIGQskS08JMT0DtjzZwt0VJFIOY2daraAKJPk0VnE9RYQdStwi4+SZmhYhQqZ/WPvFPAGiZE8HXfnkJUt7qXemmWamguA0paRT7uiHG1VkrVIT7eiB3WmixkdVgD4bbmRZzeecsQ7iJlBGk67kO1PcwD8W/rrYw2TfHKIZH4WyAf+IFZCpt5SbBCOAQeD0RO0BGIP722S3WpOAemwIDAQAB";
    }

    String getEncodedPrivateKey() {
        return "-----BEGIN RSA PRIVATE KEY-----\n" +
                       "MIIEpQIBAAKCAQEA3Oh86VPQBZSDg16xCkEulBzj6LMGjNRr3KieYLLTPC3B9TcF\n" +
                       "zHceD1WYm6zXwSVxdO2G7ANHM1/robVDkBu/aMiplbG7g+28RlRb6TetDbBLUUR+\n" +
                       "8R0GB64eZhIGYArVd21GbDU5M9TkomhBS7/ZEEud7yZ5OhE5FQjtd1vBCNTO/sFC\n" +
                       "P/6kyfjDQLyCN0KwbV0JT2zcELg1elyLEjjIoLZLB/0E+aBJy8UIgTAO6uZx4ePh\n" +
                       "QYqWOZ56TB08ymSLBQg6Sa03ZqOgSVYzaLQzn7sOWER9njLCEei0OR4qg0F3v5fN\n" +
                       "JeB85mcnu6k+nhUfKcGiyM9uwAhq1JdlAHO1IwIDAQABAoIBAQClgNj0xoI3bAkL\n" +
                       "x0nEcQlAllR55oalxA/7hakCsXdowq9p1AtYIHY47twi5d/PKQpTnBFViS8y4k5b\n" +
                       "HL69nMxO6OeE61+de+NunY0usTPJ6abEABlK83+tCVplBmQYWIWNsCTutQFiP8H7\n" +
                       "mzgwVE9/0edFYQG562VkyQeAQRHiVoiiw/N1MDTkC2vG2gaQ1K7VyUs5w/BrUqDp\n" +
                       "Y5ShA7zozct3LpDOegLxEFwk47EDz/habNM0uOKeJ2MGbgkDmSt5DraUcGxWrzKG\n" +
                       "nMHl6FLKN8+s/00I4wuL5wAS477JfdAmrXwW97/sKP/aFgtGDQyBuucOBlWq82Fi\n" +
                       "WB6tJVfBAoGBAP1R28xHBJdNfpOiq/dftXYhQ0+J8rPhwhdjame4MhIeOl1Sr5xi\n" +
                       "QsCuW33HBzFWOVdT/SDddsD4A64wwVjTXIT58K8qQvRyZpBYRIWIiOB1Yvqd6XP9\n" +
                       "v9yepd+WAiEtEff882qQAsaTeSMRkLJ9LIF/bDoKDHQXoY/67ALGS2QJAoGBAN8+\n" +
                       "1t8Ola+IB5DQBlYwJjmDQyj6LuDh995ATK6EQak+Kyd4+lzIHFA554XnL/JK8n0g\n" +
                       "Ehn08EcsZ7dd/IWLR+3sQZxVNNNx5QvS38pZqURYRmG+aCp5GCGfoMjIC51R8uJ8\n" +
                       "ue9mEPsFp9xQYw5+wZpbELpuUKwcFMmCrHvUvNLLAoGBANCBa1BF+EJtd80fLTqt\n" +
                       "HZiBIn06h3mGThKgMrnyg8wj81hTMdafjmA4Y3gwtPvoA8ScjhJaF2AYBDeBS/PH\n" +
                       "7TRK4c/cEkZQT5lp/eSHkFpoZLLx/XEKoLyAHPne6BvUlCCxpM3GgXJfemriD17K\n" +
                       "5zIj5roTAVMhDEuNdmuGOhG5AoGAarBFT+RQdMq1kudXn+jy25l3hyXJMX/MTVK9\n" +
                       "Usmx94fWZ87RF3Yq0cxacQvDRi+7I0EoZW9BRUJbbq5j+A5QGcxGrsepr0NHCxeE\n" +
                       "C9g9pIPrXtr4PRQSDD+VP18a7dw0Dzk8QsSsaMqTJh7kuBCrxXCv1ejSdO4WoRbq\n" +
                       "B2PruXECgYEAsIqAi1dqF8e8OqlHPziGUvjtpWFZD6Fs57rE3iSCMKQOv6eAdzyx\n" +
                       "3WJc0xqCf4QKEL7E/Z7THbGSNWqM22EfmCh4hd3poOdfbfhrDDWasC+ku09QHdyw\n" +
                       "8BFtEm5956Jil36XRNQs3SQ2En7I2ExX117HFTJff0VH0q67agZTWh0=\n" +
                       "-----END RSA PRIVATE KEY-----";
    }



}
*/
