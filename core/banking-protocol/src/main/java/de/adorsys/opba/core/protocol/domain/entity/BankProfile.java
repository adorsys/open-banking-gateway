package de.adorsys.opba.core.protocol.domain.entity;

import de.adorsys.opba.tppbankingapi.search.model.BankProfileDescriptor;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final BankProfile.ToBankProfileDescriptor TO_BANK_PROFILE_DESCRIPTOR = Mappers.getMapper(BankProfile.ToBankProfileDescriptor.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_profile_id_generator")
    @SequenceGenerator(name = "bank_profile_id_generator", sequenceName = "bank_profile_id_sequence")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_uuid", referencedColumnName = "uuid")
    private Bank bank;

    private String url;
    private String adapterId;
    private String idpUrl;
    private String scaApproaches;
    private String services;

    @Mapper
    public interface ToBankProfileDescriptor {
        @Mapping(source = "bank.name", target = "bankName")
        @Mapping(source = "bank.bic", target = "bic")
        @Mapping(source = "bank.uuid", target = "bankUuid")
        @Mapping(expression = "java("
                + "java.util.Arrays.asList("
                + "bankProfile.getServices() == null ? new String[0] : bankProfile.getServices().split(\",\")"
                + "))",
                target = "serviceList")
        BankProfileDescriptor map(BankProfile bankProfile);
    }
}
