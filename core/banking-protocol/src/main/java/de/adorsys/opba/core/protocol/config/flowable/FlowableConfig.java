package de.adorsys.opba.core.protocol.config.flowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.api.types.VariableType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Map;

@Configuration
public class FlowableConfig {

    @Bean
    EngineConfigurationConfigurer<SpringAppEngineConfiguration> storeCustomClassesAsJson(
            @Value("${opba.flowable.serializeOnly:de.adorsys}") String serializableClassesPrefix,
            @Value("${opba.flowable.maxVarLen:2048}") int maxLength,
            ObjectMapper mapper
    ) {
        return engineConfiguration ->
                engineConfiguration.setCustomPreVariableTypes(
                        new ArrayList<>(ImmutableList.of(new AsJsonVariableType(
                                serializableClassesPrefix, mapper, maxLength))
                        )
        );
    }

    // TODO: Secure serialized values with some encryption.
    @RequiredArgsConstructor
    static class AsJsonVariableType implements VariableType {

        static final String JSON = "as_json";

        private final String allowOnlyClassesWithPrefix;
        private final ObjectMapper mapper;
        private final int maxLength;

        @Override
        public String getTypeName() {
            return JSON;
        }

        @Override
        public boolean isCachable() {
            return true;
        }

        @Override
        @SneakyThrows
        public boolean isAbleToStore(Object o) {
            if (o == null) {
                return true;
            }

            if (!o.getClass().getCanonicalName().startsWith(allowOnlyClassesWithPrefix)) {
                return false;
            }

            String value = mapper.writeValueAsString(o);
            return value.length() < maxLength;
        }

        @Override
        @SneakyThrows
        public void setValue(Object o, ValueFields valueFields) {
            valueFields.setTextValue(mapper.writeValueAsString(ImmutableMap.of(
                    o.getClass().getCanonicalName(),
                    o
            )));
        }

        @Override
        @SneakyThrows
        public Object getValue(ValueFields valueFields) {
            JsonNode value = mapper.readTree(valueFields.getTextValue());
            Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

            if (!classNameAndValue.getKey().startsWith(allowOnlyClassesWithPrefix)) {
                throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
            }

            return mapper.readValue(
                    classNameAndValue.getValue().traverse(),
                    Class.forName(classNameAndValue.getKey())
            );
        }
    }
}
