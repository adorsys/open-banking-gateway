package de.adorsys.opba.fintech.impl.database.entities;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
public class SessionEntity {
    public SessionEntity(UserEntity userEntity, int maxAge) {
        this.validUntil = OffsetDateTime.now().minusSeconds(maxAge);
        this.consentConfirmed = false;
        this.userEntity = userEntity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_generator")
    @SequenceGenerator(name = "session_generator", sequenceName = "session_id_seq")
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime validUntil;
    private String authId;
    private String sessionCookieValue;
    private UUID tppServiceSessionId;

    // each time user logs in, user gets new session
    // might be for different devices or different tabs
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

    @Column(nullable = false)
    private Boolean consentConfirmed;

    @SneakyThrows
    public static String createSessionCookieValue(String xsrfToken) {
        ObjectMapper mapper = new ObjectMapper();
        return URLEncoder.encode(mapper.writeValueAsString(new SessionCookieValue(hashAndHexconvert(xsrfToken))), JsonEncoding.UTF8.getJavaName());
    }

    @SneakyThrows
    public static void validateSessionCookieValue(String sessionCookieValueString, String xsrfToken) {
        String decode = URLDecoder.decode(sessionCookieValueString, JsonEncoding.UTF8.getJavaName());
        ObjectMapper mapper = new ObjectMapper();
        SessionCookieValue sessionCookieValue = mapper.readValue(decode, SessionCookieValue.class);
        if (sessionCookieValue.getHashedXsrfToken().equals(hashAndHexconvert(xsrfToken))) {
            log.info("validation of token for session ok {}", sessionCookieValue);
            return;
        }
        throw new RuntimeException("session cookie not valid " + sessionCookieValue);
    }

    @Data
    public static class SessionCookieValue {
        private final String hashedXsrfToken;
    }

    @SneakyThrows
    static String hashAndHexconvert(String decoded) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                decoded.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(encodedhash);
    }

}
