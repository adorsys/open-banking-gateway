package de.adorsys.opba.helpers.protocol.testing.service;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.common.CurrentFintechProfile;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.TransientStorage;
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreValidationRule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MapBasedRequestScopedServicesProvider implements RequestScopedServicesProvider {

    private final Map<String, RequestScoped> requestScopedInMem = new ConcurrentHashMap<>();

    @Override
    public RequestScoped findRegisteredByKeyId(String keyId) {
        return requestScopedInMem.computeIfAbsent(keyId, NoOpRequestScoped::new);
    }

    public void updateRequestScopedFor(String keyId, RequestScoped requestScoped) {
        requestScopedInMem.put(keyId, requestScoped);
    }

    @Getter
    @RequiredArgsConstructor
    public static class NoOpRequestScoped implements RequestScoped {

        private final String encryptionKeyId;
        private final TransientStorage transientStorage = new TransientStorageImpl();

        @Override
        public CurrentBankProfile aspspProfile() {
            return null;
        }

        @Override
        public ConsentAccess consentAccess() {
            return null;
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
}
