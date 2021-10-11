package de.adorsys.opba.db.domain.converter;

import de.adorsys.opba.protocol.api.common.ResultContentType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class ResultContentTypeConverter implements AttributeConverter<ResultContentType, String> {
    @Override
    public String convertToDatabaseColumn(ResultContentType resultContentType) {
        if (resultContentType == null) {
            return null;
        }
        return resultContentType.getValue();
    }

    @Override
    public ResultContentType convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return Stream.of(ResultContentType.values())
            .filter(it -> it.getValue().equals(s))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
