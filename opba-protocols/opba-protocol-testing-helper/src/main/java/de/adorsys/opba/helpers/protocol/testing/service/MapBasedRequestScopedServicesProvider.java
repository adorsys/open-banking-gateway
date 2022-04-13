package de.adorsys.opba.helpers.protocol.testing.service;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.common.CurrentFintechProfile;
import de.adorsys.opba.protocol.api.common.ResultContentType;
import de.adorsys.opba.protocol.api.common.SupportedConsentType;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.TransientStorage;
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreValidationRule;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MapBasedRequestScopedServicesProvider implements RequestScopedServicesProvider {

    private final Map<String, NoOpRequestScoped> requestScopedInMem = new ConcurrentHashMap<>();

    @Override
    public RequestScoped findRegisteredByKeyId(String keyId) {
        return requestScopedInMem.computeIfAbsent(keyId, NoOpRequestScoped::new);
    }

    public NoOpRequestScoped getRequestScopedFor(String keyId) {
        return requestScopedInMem.computeIfAbsent(keyId, NoOpRequestScoped::new);
    }

    public void updateRequestScopedFor(String keyId, NoOpRequestScoped requestScoped) {
        requestScopedInMem.put(keyId, requestScoped);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class NoOpRequestScoped implements RequestScoped {

        private final String encryptionKeyId;

        private final TransientStorage transientStorage = new TransientStorageImpl();
        private BankProfile bankProfile = new BankProfile();
        private ConsentAccessor consentAccessor = new ConsentAccessor();

        @Override
        public CurrentBankProfile aspspProfile() {
            return bankProfile;
        }

        @Override
        public ConsentAccess consentAccess() {
            return consentAccessor;
        }

        @Override
        public PaymentAccess paymentAccess() {
            return null;
        }

        @Override
        public EncryptionService encryption() {
            return new NoOpEncryptionService(encryptionKeyId);
        }

        @Override
        public CurrentFintechProfile fintechProfile() {
            return () -> "DUMMY-FINTECH";
        }

        @Override
        public FieldsToIgnoreLoader fieldsToIgnoreLoader() {
            return new FieldsToIgnoreLoader() {
                @Override
                public <T> Map<FieldCode, IgnoreValidationRule> getIgnoreValidationRules(Class<T> invokerClass, Approach approach) {
                    return Map.of();
                }
            };
        }

        @Override
        public TransientStorage transientStorage() {
            return transientStorage;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class NoOpEncryptionService implements EncryptionService {

        private final String encryptionKeyId;

        @Override
        public byte[] encrypt(byte[] data) {
            return data;
        }

        @Override
        public byte[] decrypt(byte[] data) {
            return data;
        }
    }

    public static class TransientStorageImpl implements TransientStorage {

        @Delegate
        @SuppressWarnings("PMD.UnusedPrivateField") // it is used through Delegate - via TransientStorage interface
        private final AtomicReference<Object> value = new AtomicReference<>();
    }

    @Data
    public static class BankProfile implements CurrentBankProfile {
        private Long id;
        private String url;
        private String adapterId;
        private String idpUrl;
        private UUID uuid;
        private List<Approach> scaApproaches;
        private List<SupportedConsentType> supportedConsentTypes;
        private Approach preferredApproach;
        private boolean tryToUsePreferredApproach;
        private boolean uniquePaymentPurpose;
        private boolean xs2aSkipConsentAuthorization;
        private boolean xs2aStartConsentAuthorizationWithPin;
        private String supportedXs2aApiVersion;
        private String bic;
        private String bankCode;
        private String name;
        private String externalId;
        private String externalInterfaces;
        private String bankName;
        private String protocolConfiguration;
        private ResultContentType contentTypeTransactions;
    }

    @Data
    public static class ConsentAccessor implements ConsentAccess {

        private ProtocolFacingConsent consent;
        private boolean finTechScope;

        @Override
        public ProtocolFacingConsent createDoNotPersist() {
            return new Consent();
        }

        @Override
        public void save(ProtocolFacingConsent consent) {
            this.consent = consent;
        }

        @Override
        public void delete(ProtocolFacingConsent consent) {
           this.consent = null;
        }

        @Override
        public List<ProtocolFacingConsent> findByCurrentServiceSessionOrderByModifiedDesc() {
            return Stream.of(consent).filter(Objects::nonNull).collect(Collectors.toList());
        }

        @Override
        public Optional<ProtocolFacingConsent> findSingleByCurrentServiceSession() {
            return Optional.ofNullable(consent);
        }

        @Override
        public Collection<ProtocolFacingConsent> getAvailableConsentsForCurrentPsu() {
            return Stream.of(consent).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    @Data
    public static class Consent implements ProtocolFacingConsent {

        private String consentId;
        private String consentContext;
        private String consentCache;

        @Override
        public EncryptionService getSupplementaryEncryptionService() {
            return null;
        }
    }
}
