package de.adorsys.opba.db.domain.entity.fintech;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.opba.db.domain.entity.psu.PsuConsent;
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
import javax.persistence.ManyToOne;
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
public class FintechUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fintech_user_id_generator")
    @SequenceGenerator(name = "fintech_user_id_generator", sequenceName = "fintech_user_id_sequence")
    private Long id;

    private String psuFintechId;

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Fintech fintech;

    public UserID getUserId() {
        return new UserID(String.valueOf(id));
    }

    public UserIDAuth getUserIdAuth(Supplier<char[]> password) {
        return new UserIDAuth(String.valueOf(id), password);
    }
}
