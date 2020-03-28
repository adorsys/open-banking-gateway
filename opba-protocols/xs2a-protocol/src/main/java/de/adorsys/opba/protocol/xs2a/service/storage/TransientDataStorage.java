package de.adorsys.opba.protocol.xs2a.service.storage;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransientDataStorage {

    private final Map<String, DataEntry> entries;

    public DataEntry get(BaseContext context) {
        return entries.get(context.getSagaId());
    }

    public void set(BaseContext context, DataEntry entry) {
        entries.put(context.getSagaId(), entry);
    }

    @Getter
    @RequiredArgsConstructor
    public static class DataEntry {

        private final String psuPassword;
        private final String scaChallengeResult;
    }
}
