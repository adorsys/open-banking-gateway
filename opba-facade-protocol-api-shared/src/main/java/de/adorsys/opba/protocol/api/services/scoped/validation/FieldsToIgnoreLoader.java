package de.adorsys.opba.protocol.api.services.scoped.validation;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;

import java.util.Map;

public interface FieldsToIgnoreLoader {
    <T> Map<FieldCode, IgnoreValidationRule> getIgnoreValidationRules(Class<T> invokerClass, Approach approach);
}
