package de.adorsys.opba.core.protocol.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * XA-enabled DataSource, mostly to separate business entities from BPMN-managed entities.
 */
@Configuration
public class DataSourceConfig {

    /**
     * This DataSource manages business entities.
     */
    @Primary
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.banking-protocol")
    public AtomikosDataSourceBean bankingProtocolDataSource() {
        return new AtomikosDataSourceBean();
    }

    /**
     * This DataSouece manages Flowable tables.
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @Qualifier("flowableDataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.flowable")
    public AtomikosDataSourceBean flowableDataSource() {
        return new AtomikosDataSourceBean();
    }
}
