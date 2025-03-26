package de.adorsys.opba.fintech.impl.database.entities;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
public class SessionEntity {
    public SessionEntity(UserEntity userEntity, int maxAge, Long parentSession) {
        this.validUntil = OffsetDateTime.now().plusSeconds(maxAge);
        this.userEntity = userEntity;
        this.parentSession = parentSession;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_generator")
    @SequenceGenerator(name = "session_generator", sequenceName = "session_id_seq")
    private Long id;

    private Long parentSession;

    @Column(nullable = false)
    private OffsetDateTime validUntil;
    private String sessionCookieValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

    @SneakyThrows
    public static String createSessionCookieValue(String xsrfToken) {
        ObjectMapper mapper = new ObjectMapper();
        return URLEncoder.encode(mapper.writeValueAsString(new SessionCookieValue(hashAndHexconvert(xsrfToken))), JsonEncoding.UTF8.getJavaName());
    }

    @SneakyThrows
    public static void validateSessionCookieValue(String sessionCookieValueString, String xsrfToken, RestRequestContext restRequestContext) {
        String decode = URLDecoder.decode(sessionCookieValueString, JsonEncoding.UTF8.getJavaName());
        ObjectMapper mapper = new ObjectMapper();
        SessionCookieValue sessionCookieValue = mapper.readValue(decode, SessionCookieValue.class);
        if (sessionCookieValue.getHashedXsrfToken().equals(hashAndHexconvert(xsrfToken))) {
            log.debug("validation of token for session ok {}", sessionCookieValue);
            return;
        }
        throw new RuntimeException("(redirect or) session cookie xsrftoken hash:\"" + sessionCookieValue.getHashedXsrfToken()
                + "\" does not match xsrf tokenhash \"" + hashAndHexconvert(xsrfToken) + "\" for " + restRequestContext);
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
