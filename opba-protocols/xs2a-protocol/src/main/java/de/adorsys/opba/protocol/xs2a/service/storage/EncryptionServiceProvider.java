package de.adorsys.opba.protocol.xs2a.service.storage;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EncryptionServiceProvider {

    private final Map<String, EncryptionService> entries;

    public EncryptionService get(String id) {
        return entries.get(id);
    }

    public EncryptionService get(BaseContext context) {
        return entries.get(context.getSagaId());
    }

    public void set(BaseContext context, EncryptionService entry) {
        entries.put(context.getSagaId(), entry);
    }
}
