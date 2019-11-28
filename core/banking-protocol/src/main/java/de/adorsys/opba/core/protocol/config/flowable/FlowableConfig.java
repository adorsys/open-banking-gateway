package de.adorsys.opba.core.protocol.config.flowable;

import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlowableConfig {

    @Bean
    @Qualifier("flowableDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.flowable")
    public DataSource flowableDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    EngineConfigurationConfigurer<SpringAppEngineConfiguration> EngineConfigurationConfigurer(
            @Qualifier("flowableDatasource") DataSource dataSource) {
        return engineConfiguration -> engineConfiguration.setDataSource(dataSource);
    }
}
