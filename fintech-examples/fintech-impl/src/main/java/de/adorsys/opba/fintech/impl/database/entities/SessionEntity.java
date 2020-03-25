package de.adorsys.opba.fintech.impl.database.entities;

import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public class SessionEntity {
    @Id
    private String loginUserName;
    private String fintechUserId;
    private String password;
    private String xsrfToken;
    private String psuConsentSession;
    private UUID serviceSessionId;

    // TODO orphanRemoval should be true, but thatn deleting  fails. Dont know hot to
    // test with different transactions yet
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<LoginEntity> logins = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "session_cookie_name")),
            @AttributeOverride(name = "value", column = @Column(name = "session_cookie_value"))
    })
    private CookieEntity sessionCookie;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "redirect_cookie_name")),
            @AttributeOverride(name = "value", column = @Column(name = "redirect_cookie_value"))
    })
    private CookieEntity redirectCookie;


    public SessionEntity setSessionCookieValue(String value) {
        sessionCookie = CookieEntity.builder().name(Consts.COOKIE_SESSION_COOKIE_NAME).value(value).build();
        return this;
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
}
