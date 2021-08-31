package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.tppbankingapi.search.model.generated.BankDescriptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank", uniqueConstraints = {@UniqueConstraint(columnNames = "uuid", name = "opb_bank_uuid_key")})
public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Bank.ToBankDescriptor TO_BANK_DESCRIPTOR = Mappers.getMapper(Bank.ToBankDescriptor.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_id_generator")
    @SequenceGenerator(name = "bank_id_generator", sequenceName = "bank_id_sequence")
    private Long id;

    private UUID uuid;
    private String name;
    private String bic;
    private String bankCode;
    private boolean isActive;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<BankProfile> profiles;

    @OneToMany(mappedBy = "aspsp", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Consent> consents;

    @Mapper(uses = BankProfile.ToBankProfileDescriptor.class)
    public interface ToBankDescriptor {
        @Mapping(source = "name", target = "bankName")
        @Mapping(source = "active", target = "isActive")
        BankDescriptor map(Bank bank);
    }
}
