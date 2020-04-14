//
//package de.adorsys.opba.fintech.impl.service;
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.junit.jupiter.api.Test;
//import sun.security.rsa.RSAPrivateCrtKeyImpl;
//import sun.security.rsa.RSAPublicKeyImpl;
//
//import java.security.InvalidKeyException;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.time.Instant;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.Base64;
//import java.util.UUID;
//
//public class JwtRsaTest {
//
//    @Test
//    public void testJWTWithRsa() throws NoSuchAlgorithmException, InvalidKeyException {
//        //KeyPair newPair = generateKeyPair(2048);
//
//        String encodedPublicKey = getEncodedPublicKey();
//        String encodedPrivateKey = getEncodedPrivateKey();
//
//        RSAPrivateKey privateKey = RSAPrivateCrtKeyImpl.newKey(Base64.getDecoder().decode(encodedPrivateKey));
//        RSAPublicKey publicKey = new RSAPublicKeyImpl(Base64.getDecoder().decode(encodedPublicKey));
//
//        System.out.println("Public Key:");
//        System.out.println(convertToPublicKey(encodedPublicKey));
//
//        System.out.println("Privet Key:");
//        System.out.println(convertToPrivetKey(encodedPrivateKey));
//
//        String id = UUID.randomUUID().toString();
//        OffsetDateTime time = Instant.now().atOffset(ZoneOffset.UTC);
//
//        String token = generateJwtToken(privateKey, id + time);
//
//        System.out.println("TOKEN:");
//        System.out.println(token);
//        printStructure(token, publicKey);
//
//        parseJWT(token);
//    }
//
//    private void parseJWT(String jwt) throws InvalidKeyException {
//
//        String encodedPublicKey = getEncodedPublicKey();
//        RSAPublicKey publicKey = new RSAPublicKeyImpl(Base64.getDecoder().decode(encodedPublicKey));
//
//        Claims claims = Jwts.parser()
//                                .setSigningKey(publicKey)
//                                .parseClaimsJws(jwt).getBody();
//
//        System.out.println("============================== PARSED ==========================================");
//        System.out.println("ID: " + claims.getId());
//        System.out.println("Subject: " + claims.getSubject());
//        System.out.println("Issuer: " + claims.getIssuer());
//        System.out.println("Expiration: " + claims.getExpiration());
//        System.out.println("Sing data : " + claims.get("sign-data"));
//    }
//
//
//    @SuppressWarnings("deprecation")
//    public String generateJwtToken(PrivateKey privateKey, String signData) {
//        return Jwts.builder()
//                       .setSubject("fintech")
//                       .setIssuer("fintech@awesome-fintech.com")
//                       .claim("sign-data", signData)
//                       .signWith(SignatureAlgorithm.RS256, privateKey).compact();
//    }
//
//    //Print structure of JWT
//    public void printStructure(String token, PublicKey publicKey) {
//        Jws parseClaimsJws = Jwts.parser().setSigningKey(publicKey)
//                                     .parseClaimsJws(token);
//
//        System.out.println("Header     : " + parseClaimsJws.getHeader());
//        System.out.println("Body       : " + parseClaimsJws.getBody());
//        System.out.println("Signature  : " + parseClaimsJws.getSignature());
//    }
//
//
//    // Add BEGIN and END comments
//    private String convertToPublicKey(String key) {
//        StringBuilder result = new StringBuilder();
//        result.append("-----BEGIN PUBLIC KEY-----\n");
//        result.append(key);
//        result.append("\n-----END PUBLIC KEY-----");
//        return result.toString();
//    }
//
//    // Add BEGIN and END comments
//    private String convertToPrivetKey(String key) {
//        StringBuilder result = new StringBuilder();
//        result.append("-----BEGIN RSA PRIVATE KEY-----\n");
//        result.append(key);
//        result.append("\n-----END RSA PRIVATE KEY-----");
//        return result.toString();
//    }
//
//
//    String getEncodedPublicKey() {
//        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmn9/0kqSYqUmYcohClzkBMXLGoIXB5KPFRtxO2JIbpQplWbk7LYNAO/1VVke+4UjhyyfHm2lUFF8/JxJYlTbVeuM+p3GibdlnOzH5XxPD+I/K5t3MXQ+6Lnxq81QYOthU9borTIGQskS08JMT0DtjzZwt0VJFIOY2daraAKJPk0VnE9RYQdStwi4+SZmhYhQqZ/WPvFPAGiZE8HXfnkJUt7qXemmWamguA0paRT7uiHG1VkrVIT7eiB3WmixkdVgD4bbmRZzeecsQ7iJlBGk67kO1PcwD8W/rrYw2TfHKIZH4WyAf+IFZCpt5SbBCOAQeD0RO0BGIP722S3WpOAemwIDAQAB";
//    }
//
//    String getEncodedPrivateKey() {
//        return  "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCaf3/SSpJipSZhyiEKXOQExcsaghcHko8VG3E7YkhulCmVZuTstg0A7/VVWR77hSOHLJ8ebaVQUXz8nEliVNtV64z6ncaJt2Wc7MflfE8P4j8rm3cxdD7oufGrzVBg62FT1uitMgZCyRLTwkxPQO2PNnC3RUkUg5jZ1qtoAok+TRWcT1FhB1K3CLj5JmaFiFCpn9Y+8U8AaJkTwdd+eQlS3upd6aZZqaC4DSlpFPu6IcbVWStUhPt6IHdaaLGR1WAPhtuZFnN55yxDuImUEaTruQ7U9zAPxb+utjDZN8cohkfhbIB/4gVkKm3lJsEI4BB4PRE7QEYg/vbZLdak4B6bAgMBAAECggEAQTfLLGlATBXtEuCxe99kfcNFDmaV9CNg9QcFkIzXiJ5Qw3mQ73+WE6w3wxSA6Kn2KmyiqsScQUB042nR7IlnoWhytaR+l8nsQgcOBwink0vtYKoa8axhlfpQUzazVIZoKm0RAXOZgv3ATdW6QsZkwcy4XhUIWXmHv4VIC/ruFnborwF0JhuS8kcOcRxc3OuIeKbivL4XXCM7Q6/XFL7KRCzUwMJ4IBQJMBAiO8ky+seNHQWqn+Ej8heaHAOflP2fPSLf8NZaLcuvioqHO4StYEyWrlrP0HQ5VbscCSU9/8Q4B0TzTxlVGJTZO9VB/asL/D+Tk+RawsXtfUtZ4crtKQKBgQDf7fj+EhRbhW/LHMFk98/K9fUZkAXCysKVoCfcQgjuTisHxdoR1GXgjhDwOvLvr5m4TrI73AccMNnlvWjihCLC+FpAPVMyszwbmhF4UkFT23SU/CytF+JvlS/4kfde3xGavOoy9dw9Hs2IHIocNobTMtG8/epRUcxNLlwGJCzhtwKBgQCwn+2Z8egxpmCdUPkLf6IY5YpWMbYucOci6hoLLw72Iktw1ZqzCwOmEWn+5ud+HC6vz3YSEu+ZpqX7AXe9IgaiSLUiIU4hZVTIHNTF28tBhIxUgluwdNaqS28Wi/TvFWcFHJ2raok33ExhtdwPZ6xP1MLXeClNQPkyFzCKp8xaPQKBgH9gX3UulZPl2EyiI8QqTnG17ODIku5V5c+01Vet3GSp5EI8oinoNrdAOJReA9ihe4Bii3IPW6AGjbvoPSQ0Y42iNbw65ft+Bt/wiKV3rJ9lwvAIbeVcI7qzEyGC7kJyAxKRCWtJfSjrP6CAE3Ou9IxoaPPnP4VYUS1KBKCdrUYLAoGBAKMFKZ+CSIazAs+Qshzr0+hYzKMbxQP+T0fJfPo/l/aT1r+xV9TTYK3Buux7oMSkLG8288pg0ecmimIvi530rtzQcOasiNmIFoWRHs30PRkexwslOo2WeZ+6ejD8Qrj9LxXnwJs7fKUjXxh8az2IueEBBkkr7isQwjB6eOIxWAsdAoGAYBP2t9YfGUxVzQ949rVnpVWuscfMYgy0SCVZ5+BzeYMBJFHfXY0u9OlKG5i61oqsvdlbKwHgT1EA+iraIt6AxB9ilSoewqEE6vjAVYelhvHWnYVuOEideCR55FSiAIiDekjZNE5S/ggp/9i6gd8Ly+aX5KMHu1td35GNgCuxGZw=";
//    }
//
//
//    private KeyPair generateKeyPair(int size) throws NoSuchAlgorithmException {
//        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
//        SecureRandom random = SecureRandom.getInstanceStrong();
//
//        keyGenerator.initialize(size, random);
//
//        KeyPair kp = keyGenerator.genKeyPair();
//
//        //PublicKey publicKey = (PublicKey) kp.getPublic();
//        //PrivateKey privateKey = (PrivateKey) kp.getPrivate();
//
//        //String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
//        //String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
//
//        return kp;
//    }
//
//
//}*/
