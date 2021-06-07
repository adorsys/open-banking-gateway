package de.adorsys.opba.db.domain.converter;

import de.adorsys.opba.protocol.api.common.Approach;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter
public class ScaApproachConverter implements AttributeConverter<List<Approach>, String> {
    @Override
    public String convertToDatabaseColumn(List<Approach> services) {
        return Arrays.stream(Approach.values())
                .map(Approach::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Approach> convertToEntityAttribute(String attribute) {
        if (null == attribute) {
            return Collections.emptyList();
        }

        return Stream.of(StringUtils.split(attribute, ","))
                .map(Approach::valueOf)
                .collect(Collectors.toList());
    }
}
