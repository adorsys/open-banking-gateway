package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.converter.ResultContentTypeConverter;
import de.adorsys.opba.db.domain.converter.ScaApproachConverter;
import de.adorsys.opba.db.domain.converter.SupportedConsentTypeConverter;
import de.adorsys.opba.db.domain.entity.helpers.UuidMapper;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.common.ResultContentType;
import de.adorsys.opba.protocol.api.common.SupportedConsentType;
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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
    private boolean xs2aStartConsentAuthorizationWithPin;
    private String supportedXs2aApiVersion;
    private String externalId;
    private String externalInterfaces;
    private String protocolType;

    /**
     * Customary profile-level protocol configuration. Allows to configure protocol-specific behavior (i.e.
     * limiting account types to access).
     */
    private String protocolConfiguration;

    private boolean isSandbox;
    private boolean active;

    @Convert(converter = SupportedConsentTypeConverter.class)
    private List<SupportedConsentType> supportedConsentTypes;

    @Convert(converter = ResultContentTypeConverter.class)
    private ResultContentType contentTypeTransactions;

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
        @Mapping(source = "active", target = "isActive")
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
