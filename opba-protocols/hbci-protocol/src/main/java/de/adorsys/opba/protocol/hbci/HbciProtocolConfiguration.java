package de.adorsys.opba.protocol.hbci;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableRetry
@EnableConfigurationProperties
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "de.adorsys.opba.protocol.hbci.config",
        "de.adorsys.opba.protocol.hbci.service",
        "de.adorsys.opba.protocol.hbci.entrypoint"
})
public class HbciProtocolConfiguration {
}
