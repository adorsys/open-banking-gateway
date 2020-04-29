package de.adorsys.opba.protocol.api.services.scoped.validation;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;

public interface IgnoreFieldsLoader {
    void setProtocolId(Long protocolId);
    boolean apply(FieldCode fieldCode, Class invokerClass, Approach approach);
}
