package de.adorsys.opba.db.domain.entity.psu;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.Collection;
import java.util.function.Supplier;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "psu")
    private Collection<PsuConsent> privateStore;

    @OneToMany(mappedBy = "psu", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<AuthSession> authSessions;

    public UserID getUserId() {
        return new UserID(String.valueOf(id));
    }

    public UserIDAuth getUserIdAuth(Supplier<char[]> password) {
        return new UserIDAuth(String.valueOf(id), password);
    }
}
