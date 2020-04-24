package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class FlowableConfig {

    /**
     * Customizes flowable so that it can store custom classes (not ones that implement Serializable) as
     * JSON as variables in database.
     */
    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customizeListenerAndJsonSerializer(
        RequestScopedServicesProvider scopedServicesProvider,
        Xs2aFlowableProperties flowableProperties,
        Xs2aObjectMapper mapper
    ) {
        int maxLength = flowableProperties.getMaxLength();

        return processConfiguration -> {
            processConfiguration.setCustomPreVariableTypes(
                new ArrayList<>(
                    ImmutableList.of(
                        new JsonCustomSerializer(scopedServicesProvider, mapper.getMapper(), flowableProperties.getSerializeOnlyPackages(), maxLength),
                        new LargeJsonCustomSerializer(scopedServicesProvider, mapper.getMapper(), flowableProperties.getSerializeOnlyPackages(), maxLength)
                    )
                )
            );
            processConfiguration.setEnableEventDispatcher(true);
        };
    }

    /**
     * Dedicated ObjectMapper to be used in XS2A protocol.
     */
    @Bean
    Xs2aObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Ignoring getters and setters as we are using 'rich' domain model:
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        return new Xs2aObjectMapper(mapper);
    }
}
