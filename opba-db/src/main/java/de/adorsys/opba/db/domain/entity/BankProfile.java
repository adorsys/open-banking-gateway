package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.converter.ScaApproachConverter;
import de.adorsys.opba.db.domain.entity.helpers.UuidMapper;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.tppbankingapi.search.model.generated.BankProfileDescriptor;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankProfile implements Serializable, CurrentBankProfile {
    private static final long serialVersionUID = 1L;

    public static final BankProfile.ToAspsp TO_ASPSP = Mappers.getMapper(BankProfile.ToAspsp.class);
    public static final BankProfile.ToBankProfileDescriptor TO_BANK_PROFILE_DESCRIPTOR =
            Mappers.getMapper(BankProfile.ToBankProfileDescriptor.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_profile_id_generator")
    @SequenceGenerator(name = "bank_profile_id_generator", sequenceName = "bank_profile_id_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_uuid", referencedColumnName = "uuid")
    private Bank bank;

    private String url;
    private String adapterId;
    private String idpUrl;
    private UUID uuid;
    private String name;

    @Convert(converter = ScaApproachConverter.class)
    private List<Approach> scaApproaches;

    @Enumerated(EnumType.STRING)
    private Approach preferredApproach;
    private boolean tryToUsePreferredApproach;
    private boolean uniquePaymentPurpose;
    private boolean xs2aSkipConsentAuthorization;
    private String externalId;
    private String externalInterfaces;
    private String protocolType;
    private boolean isSandbox;

    @OneToMany(mappedBy = "bankProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "protocolAction")
    private Map<ProtocolAction, BankAction> actions = new HashMap<>();

    @OneToMany(mappedBy = "bankProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<ServiceSession> servicesSessions;

    @Mapper(uses = {ToConsentSupported.class, UuidMapper.class})
    public interface ToBankProfileDescriptor {
        @Mapping(source = "bank.name", target = "bankName")
        @Mapping(source = "bank.bic", target = "bic")
        @Mapping(source = "bank.uuid", target = "bankUuid")
        @Mapping(expression = "java(bankProfile.getActions().keySet().stream()"
                + ".map(Enum::name)"
                + ".collect(java.util.stream.Collectors.toList()))",
                target = "serviceList")
        @Mapping(source = "actions", target = "consentSupportByService")
        @Mapping(source = "sandbox", target = "isSandbox")
        BankProfileDescriptor map(BankProfile bankProfile);
    }

    @Mapper(uses = UuidMapper.class)
    public interface ToAspsp {
        @Mapping(source = "bank.name", target = "name")
        @Mapping(source = "bank.bic", target = "bic")
        @Mapping(source = "bank.uuid", target = "bankCode")
        @Mapping(expression = "java("
                + "bankProfile.getScaApproaches().stream()"
                + ".map(a -> de.adorsys.xs2a.adapter.api.model.AspspScaApproach.valueOf(a.name()))"
                + ".collect(java.util.stream.Collectors.toList()))",
                target = "scaApproaches")
        Aspsp map(BankProfile bankProfile);
    }

    @Mapper(uses = UuidMapper.class)
    public interface ToConsentSupported {

        default Map<String, String> map(Map<ProtocolAction, BankAction> actions) {
            return actions.entrySet().stream()
                    .collect(Collectors.toMap(it -> it.getKey().name(), it -> String.valueOf(it.getValue().isConsentSupported())));
        }
    }

    @Override
    public String getBic() {
        Bank bank = getBank();
        if (null == bank) {
            return null;
        }

        return bank.getBic();
    }

    @Override
    public String getBankCode() {
        Bank bank = getBank();
        if (null == bank) {
            return null;
        }

        return bank.getBankCode();
    }

    @Override
    public String getBankName() {
        Bank bank = getBank();
        if (null == bank) {
            return null;
        }

        return bank.getName();
    }
}
