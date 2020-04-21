package de.adorsys.opba.protocol.api.services.scoped.validation;

import java.util.List;

public interface UsesValidation {
    List<BankValidationRuleDto> getValidationRules();
}
