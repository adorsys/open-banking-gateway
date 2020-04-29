package de.adorsys.opba.api.security.external.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class DataToSign<T> {
    private final ObjectMapper localMapper = buildObjectMapper();
    private UUID xRequestId;
    private Instant instant;
    private final T additionalFields;

    public DataToSign(UUID xRequestId, Instant instant) {
        this.xRequestId = xRequestId;
        this.instant = instant;
        this.additionalFields = null;
    }

    public DataToSign(UUID xRequestId, Instant instant, T additionalFields) {
        this.xRequestId = xRequestId;
        this.instant = instant;
        this.additionalFields = additionalFields;
    }

    public String convertDataToString() {
        return new StringBuilder().append(xRequestId)
                       .append(instant)
                       .append(mapAdditionalFieldsToString())
                       .toString();
    }

    private String mapAdditionalFieldsToString() {
        if (additionalFields == null) {
            return "";
        }
        JsonNode nodes = localMapper.valueToTree(additionalFields);

        if (nodes.isArray()) {
            String message = "Error of values sorting. Additional fields should not be arrays";
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        if (nodes.isValueNode()) {
            return nodes.asText();
        }

        Map<String, String> unSortedMap = objectToUnsortedMap(nodes);
        LinkedHashMap<String, String> sortedMap = sortMap(unSortedMap);
        LinkedHashMap<String, String> filteredMap = valueNotNullFilter(sortedMap);

        String mapAsString = valueAsString(filteredMap);

        if (filteredMap.isEmpty() || mapAsString == null) {
            return "";
        }

        return mapAsString;
    }

    private Map<String, String> objectToUnsortedMap(JsonNode objectNode) {
        Map<String, String> unSortedMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();

        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();

            JsonNode value = entry.getValue();
            if (!value.isValueNode()) {
                String message = "Error of value conversion. Values should have primitive type";
                log.error(message);
                throw new IllegalArgumentException(message);
            }

            unSortedMap.put(entry.getKey(), entry.getValue().asText());
        }
        return unSortedMap;
    }

    private LinkedHashMap<String, String> sortMap(Map<String, String> unSortedMap) {
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();

        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        return sortedMap;
    }

    private LinkedHashMap<String, String> valueNotNullFilter(LinkedHashMap<String, String> sortedMap) {
        return sortedMap.entrySet()
                       .stream()
                       .filter(e -> Objects.nonNull(e.getValue()))
                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    private String valueAsString(Object value) {
        try {
            return localMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            String message = String.format("Error converting object to string %s", value);

            throw new IllegalArgumentException(message);
        }
    }

    private ObjectMapper buildObjectMapper() {
        ObjectMapper localObjectMapper = new ObjectMapper();
        localObjectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        localObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        localObjectMapper.registerModule(new JavaTimeModule());
        return localObjectMapper;
    }
}
