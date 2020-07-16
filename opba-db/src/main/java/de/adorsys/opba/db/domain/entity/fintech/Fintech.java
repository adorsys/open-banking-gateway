package de.adorsys.opba.db.domain.entity.fintech;

import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
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
public class Fintech {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fintech_id_generator")
    @SequenceGenerator(name = "fintech_id_generator", sequenceName = "fintech_id_sequence")
    private Long id;

    private String globalId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] keystore;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] pubKeys;

    @OneToMany(mappedBy = "fintech", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<FintechPsuAspspPrvKeyInbox> inbox;

    @OneToMany(mappedBy = "fintech", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<FintechPsuAspspPrvKey> consentKeys;

    @OneToMany(mappedBy = "fintech", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<FintechPrvKey> fintechOnlyPrvKeys;

    @OneToMany(mappedBy = "fintech", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<FintechUser> requestedConsentSpecs;

    public UserID getUserId() {
        return new UserID(String.valueOf(id));
    }

    public UserIDAuth getUserIdAuth(Supplier<char[]> password) {
        return new UserIDAuth(String.valueOf(id), password);
    }
}
