package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

// TODO: Use async entries in process definition, so that when some execution fails, whole transaction is not rolled back
@Configuration
public class FlowableConfig {

    /**
     * Customizes flowable so that it can store custom classes (not ones that implement Serializable) as
     * JSON as variables in database.
     */
    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customizeListenerAndJsonSerializer(
        @Value("${opba.flowable.serializeOnly:de.adorsys}") String serializableClassesPrefix,
        @Value("${opba.flowable.maxVarLen:2048}") int maxLength,
        Xs2aObjectMapper mapper,
        FlowableJobEventListener eventListener
    ) {
        return processConfiguration -> {
            processConfiguration.setCustomPreVariableTypes(
                new ArrayList<>(
                    ImmutableList.of(
                        new JsonCustomSerializer(mapper.getMapper(), serializableClassesPrefix, maxLength),
                        new LargeJsonCustomSerializer(mapper.getMapper(), serializableClassesPrefix, maxLength)
                    )
                )
            );
            processConfiguration.setEnableEventDispatcher(true);
            processConfiguration.setEventListeners(ImmutableList.of(eventListener));
        };
    }

    @Bean
    Xs2aObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Xs2aObjectMapper(mapper);
    }
}
