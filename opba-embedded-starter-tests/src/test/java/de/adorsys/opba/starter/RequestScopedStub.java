package de.adorsys.opba.starter;

import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.consent.ConsentAccess;
import de.adorsys.opba.protocol.api.services.scoped.transientdata.TransientStorage;

import java.util.concurrent.atomic.AtomicReference;

public class RequestScopedStub implements RequestScoped {

    private final TransientStorage transientStorage = new TransientStorageStub();

    @Override
    public String getEncryptionKeyId() {
        return "NOOP";
    }

    @Override
    public CurrentBankProfile aspspProfile() {
        return null;
    }

    @Override
    public ConsentAccess consentAccess() {
        return null;
    }

    @Override
    public EncryptionService encryption() {
        return null;
    }

    @Override
    public TransientStorage transientStorage() {
        return transientStorage;
    }

    private static class TransientStorageStub implements TransientStorage {

        private final AtomicReference<Object> data = new AtomicReference<>();

        @Override
        public <T> T get() {
            return (T) this.data.get();
        }

        @Override
        public void set(Object entry) {
            this.data.set(entry);
        }
    }
}
