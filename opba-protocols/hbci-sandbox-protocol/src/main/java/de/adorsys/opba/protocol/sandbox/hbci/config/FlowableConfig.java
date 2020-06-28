package de.adorsys.opba.protocol.sandbox.hbci.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
public class FlowableConfig {

    private final ObjectMapper mapper;

    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customizeListenerAndJsonSerializer() {
        return processConfiguration -> processConfiguration.setCustomPreVariableTypes(
                new ArrayList<>(
                        ImmutableList.of(new JsonCustomSerializer(mapper))
                )
        );
    }
}
