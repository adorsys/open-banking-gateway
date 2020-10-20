package de.adorsys.opba.protocol.bpmnshared.config.flowable;

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
import java.util.List;

@Configuration
public class FlowableConfig {

    /**
     * Customizes flowable so that it can store custom classes (not ones that implement Serializable) as
     * JSON as variables in database.
     */
    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> productionCustomizeListenerAndJsonSerializer(
            RequestScopedServicesProvider scopedServicesProvider,
            FlowableProperties flowableProperties,
            FlowableObjectMapper mapper,
            FlowableJobEventListener eventListener
    ) {
        int maxLength = flowableProperties.getSerialization().getMaxLength();
        List<String> serializeOnlyPackages = flowableProperties.getSerialization().getSerializeOnlyPackages();

        return processConfiguration -> {
            processConfiguration.setCustomPreVariableTypes(
                    new ArrayList<>(
                            ImmutableList.of(
                                    new JsonCustomSerializer(scopedServicesProvider, mapper.getMapper(), serializeOnlyPackages, maxLength),
                                    new LargeJsonCustomSerializer(scopedServicesProvider, mapper.getMapper(), serializeOnlyPackages, maxLength)
                            )
                    )
            );

            processConfiguration.setEnableEventDispatcher(true);
            processConfiguration.setEventListeners(ImmutableList.of(eventListener));
            processConfiguration.setAsyncExecutorNumberOfRetries(flowableProperties.getNumberOfRetries());
        };
    }

    /**
     * Dedicated ObjectMapper to be used in XS2A protocol.
     */
    @Bean
    FlowableObjectMapper mapper(List<? extends JacksonMixin> mixins) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mixins.forEach(it -> mapper.addMixIn(it.getType(), it.getMixin()));
        // Ignoring getters and setters as we are using 'rich' domain model:
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        return new FlowableObjectMapper(mapper);
    }
}
