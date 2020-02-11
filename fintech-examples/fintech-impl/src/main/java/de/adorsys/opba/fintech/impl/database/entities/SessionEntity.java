package de.adorsys.opba.fintech.impl.database.entities;

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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String password;
    private String xsrfToken;

    // TODO orphanRemoval should be true, but thatn deleting  fails. Dont know hot to
    // test with different transactions yet
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<LoginEntity> logins = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<CookieEntity> cookies = new ArrayList<>();

    @AttributeOverrides({
            @AttributeOverride(name = "okURL", column = @Column(name = "TRANS_OK_URL")),
            @AttributeOverride(name = "notOkURL", column = @Column(name = "TRANS_NOT_OK_URL"))
    })
    RedirectUrlsEmbeddable redirectListTransactions;

    @AttributeOverrides({
            @AttributeOverride(name = "okURL", column = @Column(name = "ACCOUNTS_OK_URL")),
            @AttributeOverride(name = "notOkURL", column = @Column(name = "ACCOUNTS_NOT_OK_URL"))
    })
    @Column(name = "b")
    RedirectUrlsEmbeddable redirectListAccounts;

    String psuConsentSession;

    public SessionEntity addCookie(String key, String value) {
        if (cookies == null) {
            cookies = new ArrayList<>();
        }
        cookies.add(CookieEntity.builder().name(key).value(value).build());
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
