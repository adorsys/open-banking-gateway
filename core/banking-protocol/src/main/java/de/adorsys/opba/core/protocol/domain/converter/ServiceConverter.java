package de.adorsys.opba.core.protocol.domain.converter;

import de.adorsys.opba.core.protocol.domain.Service;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        List<Service> res = new ArrayList<>();

        if (StringUtils.isEmpty(s)) {
            return res;
        }
        Stream.of(StringUtils.split(s, ",")).forEach(str -> Service.lookupByCode(str).ifPresent(res::add));
        return res;
    }
}
