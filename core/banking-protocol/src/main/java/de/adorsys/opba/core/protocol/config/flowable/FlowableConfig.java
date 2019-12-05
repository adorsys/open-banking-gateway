package de.adorsys.opba.core.protocol.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.flowable.app.spring.SpringAppEngineConfiguration;
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
    EngineConfigurationConfigurer<SpringAppEngineConfiguration> storeCustomClassesAsJson(
        @Value("${opba.flowable.serializeOnly:de.adorsys}") String serializableClassesPrefix,
        @Value("${opba.flowable.maxVarLen:2048}") int maxLength,
        ObjectMapper mapper
    ) {
        return engineConfiguration ->
            engineConfiguration.setCustomPreVariableTypes(
                new ArrayList<>(
                    ImmutableList.of(
                        new JsonCustomSerializer(mapper, serializableClassesPrefix, maxLength),
                        new LargeJsonCustomSerializer(mapper, serializableClassesPrefix, maxLength)
                    )
                )
            );
    }

    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> flowableEventListeners(
        FlowableJobSuccessEventListener eventListener
    ) {
        return processConfiguration -> {
            processConfiguration.setEnableEventDispatcher(true);
            processConfiguration.setEventListeners(ImmutableList.of(eventListener));
        };
    }
}
