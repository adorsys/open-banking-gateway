package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.repository.jpa.IgnoreValidationRuleRepository;
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IgnoreFieldsLoaderFactory {

    private final IgnoreValidationRuleRepository ignoreValidationRuleRepository;

    public FieldsToIgnoreLoader createIgnoreFieldsLoader(Long protocolId) {
        return new FieldsToIgnoreLoaderImpl(protocolId, ignoreValidationRuleRepository);
    }
}
