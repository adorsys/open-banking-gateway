package de.adorsys.opba.core.protocol.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.variable.service.impl.types.JsonType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class FlowableConfig {

    @Bean
    EngineConfigurationConfigurer<SpringAppEngineConfiguration> storeCustomClassesAsJson(
            @Value("${opba.flowable.maxVarLen:2048}") int maxLength,
            ObjectMapper mapper
    ) {
        return engineConfiguration ->
                engineConfiguration.setCustomPreVariableTypes(
                        new ArrayList<>(ImmutableList.of(new AsJsonVariableType(maxLength, mapper)))
        );
    }

    static class AsJsonVariableType extends JsonType {

        AsJsonVariableType(int maxLength, ObjectMapper objectMapper) {
            super(maxLength, objectMapper);
        }

        @Override
        public boolean isAbleToStore(Object value) {
            return true;
        }
    }
}
