package de.adorsys.opba.api.security.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class SignData<T> {
    private final ObjectMapper localMapper = buildObjectMapper();
    private UUID xRequestId;
    private OffsetDateTime requestDateTime;
    private final T additionalFields;

    public SignData(UUID xRequestId, OffsetDateTime requestDateTime) {
        this.xRequestId = xRequestId;
        this.requestDateTime = requestDateTime;
        this.additionalFields = null;
    }

    public SignData(UUID xRequestId, OffsetDateTime requestDateTime, T additionalFields) {
        this.xRequestId = xRequestId;
        this.requestDateTime = requestDateTime;
        this.additionalFields = additionalFields;
    }

    public String convertDataToString() {
        return new StringBuilder().append(xRequestId)
                       .append(requestDateTime)
                       .append(mapAdditionalFieldsToString())
                       .toString();
    }

    private String mapAdditionalFieldsToString() {
        if (additionalFields == null) {
            return "";
        }

        Map<String, Object> unSortedMap = localMapper.convertValue(additionalFields, Map.class);
        LinkedHashMap<String, String> sortedMap = sortMap(unSortedMap);
        Map<String, String> filteredMap = valueNotNullFilter(sortedMap);
        String mapAsString = valueAsString(filteredMap);

        if (filteredMap.isEmpty() || mapAsString == null) {
            return "";
        }

        return mapAsString;
    }

    private LinkedHashMap<String, String> sortMap(Map<String, Object> unSortedMap) {
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();

        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), valueAsString(x.getValue())));

        return sortedMap;
    }

    private Map<String, String> valueNotNullFilter(LinkedHashMap<String, String> sortedMap) {
        return sortedMap.entrySet()
                       .stream()
                       .filter(e -> Objects.nonNull(e.getValue()))
                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String valueAsString(Object value) {
        try {
            return localMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("Error converting object to string {}", value);
            return null;
        }
    }

    private ObjectMapper buildObjectMapper() {
        ObjectMapper localObjectMapper = new ObjectMapper();
        localObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        localObjectMapper.registerModule(new JavaTimeModule());
        return localObjectMapper;
    }
}
