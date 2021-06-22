package de.adorsys.opba.db.domain.converter;

import de.adorsys.opba.protocol.api.common.ProtocolAction;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.UNDEFINED_STUB;

/**
 * This is special converter that allows to ignore 'STUB-*' actions that are not yet used.
 * These actions are meant to ease migrations of all banks, when i.e. new action is added.
 */
@Converter
public class ProtocolActionConverter implements AttributeConverter<ProtocolAction, String> {

    @Override
    public String convertToDatabaseColumn(ProtocolAction attribute) {
        if (null == attribute) {
            return null;
        }

        return attribute.name();
    }

    @Override
    public ProtocolAction convertToEntityAttribute(String dbData) {
        if (null == dbData) {
            return null;
        }

        return Arrays.stream(ProtocolAction.values())
                .filter(it -> it.name().equals(dbData))
                .findFirst()
                .orElse(UNDEFINED_STUB);
    }
}
