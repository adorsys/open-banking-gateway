package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.repository.jpa.IgnoreBankValidationRuleRepository;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreFieldsLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IgnoreFieldsLoaderFactory {

    private final IgnoreBankValidationRuleRepository ignoreBankValidationRuleRepository;

    public IgnoreFieldsLoader createIgnoreFieldsLoader(Long protocolId) {
        return new IgnoreFieldsLoaderImpl(protocolId, ignoreBankValidationRuleRepository);
    }
}
