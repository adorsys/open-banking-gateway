package de.adorsys.opba.db.domain.converter;

import de.adorsys.opba.protocol.api.common.SupportedConsentType;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter
public class SupportedConsentTypeConverter implements AttributeConverter<List<SupportedConsentType>, String> {
    @Override
    public String convertToDatabaseColumn(List<SupportedConsentType> types) {
        if (null == types) {
            return null;
        }

        return types.stream()
                .map(SupportedConsentType::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<SupportedConsentType> convertToEntityAttribute(String attribute) {
        if (null == attribute) {
            return Collections.emptyList();
        }

        return Stream.of(StringUtils.split(attribute, ","))
                .map(SupportedConsentType::valueOf)
                .collect(Collectors.toList());
    }
}
