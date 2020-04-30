package de.adorsys.opba.protocol.api.services.scoped.validation;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;

import java.util.Map;

public interface IgnoreFieldsLoader {
    <T> Map<FieldCode, Rules> getValidationRules(Class<T> invokerClass, Approach approach);
}
