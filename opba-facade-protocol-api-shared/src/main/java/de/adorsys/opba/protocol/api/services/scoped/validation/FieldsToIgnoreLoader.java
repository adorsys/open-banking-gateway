package de.adorsys.opba.protocol.api.services.scoped.validation;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;

import java.util.Map;

/**
 * Allows to configure which fields are ignored when checking if all API parameters are available for consent.
 */
public interface FieldsToIgnoreLoader {

    /**
     * List rules that allow to ignore validation errors.
     * @param invokerClass Ignore validation errors that come from this class
     * @param approach Consent/Payment authorization approach that should be active
     * @return Field code scoped rules that allow to ignore validation errors
     */
    <T> Map<FieldCode, IgnoreValidationRule> getIgnoreValidationRules(Class<T> invokerClass, Approach approach);
}
