package de.adorsys.opba.core.protocol.config.flowable;

import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlowableConfig {

    /**
     * Forcefully sets Flowable to reside in different datasource.
     */
    @Bean
    EngineConfigurationConfigurer<SpringAppEngineConfiguration> engineConfigurationConfigurer(
            @Qualifier("flowableDataSource") DataSource dataSource) {
        return engineConfiguration -> engineConfiguration.setDataSource(dataSource);
    }
}
