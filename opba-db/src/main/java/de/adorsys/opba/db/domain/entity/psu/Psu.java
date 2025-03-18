package de.adorsys.opba.db.domain.entity.psu;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.Instant;
import java.util.Collection;
import java.util.function.Supplier;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Psu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psu_id_generator")
    @SequenceGenerator(name = "psu_id_generator", sequenceName = "psu_id_sequence")
    private Long id;

    private String login;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] keystore;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] pubKeys;

    @OneToMany(mappedBy = "psu", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<PsuAspspPrvKey> consentKeys;

    @OneToMany(mappedBy = "psu", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<AuthSession> authSessions;

    @OneToMany(mappedBy = "psu", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Consent> consents;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public UserID getUserId() {
        return new UserID(String.valueOf(id));
    }

    public UserIDAuth getUserIdAuth(Supplier<char[]> password) {
        return new UserIDAuth(String.valueOf(id), password);
    }
}
