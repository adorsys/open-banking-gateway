package de.adorsys.opba.tppbankingapi.domain.converter;

import de.adorsys.opba.tppbankingapi.domain.Service;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter
public class ServiceConverter implements AttributeConverter<List<Service>, String> {
    @Override
    public String convertToDatabaseColumn(List<Service> services) {
        return Arrays.stream(Service.values())
                .map(Service::getCode)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Service> convertToEntityAttribute(String s) {
        return Stream.of(StringUtils.split(s, ","))
                .map(Service::lookupByCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
