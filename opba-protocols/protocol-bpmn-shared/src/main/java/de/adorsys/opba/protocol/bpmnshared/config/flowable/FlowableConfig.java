package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import lombok.extern.slf4j.Slf4j;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class FlowableConfig {

    public static final int NUMBER_OF_RETRIES = 0;

    /**
     * Customizes flowable so that it can store custom classes (not ones that implement Serializable) as
     * JSON as variables in database.
     */
    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customizeListenerAndJsonSerializer(
            RequestScopedServicesProvider scopedServicesProvider,
            FlowableProperties flowableProperties,
            FlowableObjectMapper mapper
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

            // TODO: re-enable retry after proper error handling implementation
            processConfiguration.setAsyncExecutorNumberOfRetries(NUMBER_OF_RETRIES);
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
