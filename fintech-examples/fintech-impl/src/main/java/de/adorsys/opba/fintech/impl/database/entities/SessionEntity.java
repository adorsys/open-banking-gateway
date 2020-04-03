package de.adorsys.opba.fintech.impl.database.entities;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class SessionEntity {
    @Id
    private String loginUserName;
    private String fintechUserId;
    private String password;
    private String psuConsentSession;
    private UUID serviceSessionId;
    private String sessionCookieValue;
    // FIXME call 4c is missing
    @Column(nullable = false)
    private Boolean consentConfirmed;


    // TODO orphanRemoval should be true, but thatn deleting  fails. Dont know how to
    // test with different transactions yet
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<LoginEntity> logins = new ArrayList<>();



    @SneakyThrows
    public static String createSessionCookieValue(String fintechUserId, String xsrfToken) {
        // TODO Hashing with SHA256
        ObjectMapper mapper = new ObjectMapper();
        return URLEncoder.encode(mapper.writeValueAsString(new SessionCookieValue(fintechUserId, hashAndHexconvert(xsrfToken))), JsonEncoding.UTF8.getJavaName());
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

    public void addLogin(OffsetDateTime time) {
        if (logins == null) {
            logins = new ArrayList<>();
        }
        logins.add(LoginEntity.builder().loginTime(time).build());
    }

    public OffsetDateTime getLastLogin() {
        if (logins.isEmpty()) {
            throw new RuntimeException("PROGRAMMING ERROR: at least one successful login must be known yet");
        }
        int size = logins.size();
        if (size == 1) {
            return null;
        }
        return logins.get(size - 1).getLoginTime();
    }

    @Data
    public static class SessionCookieValue {
        private final String fintechUserId;
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
