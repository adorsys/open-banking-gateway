package de.adorsys.opba.protocol.xs2a;

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
        "de.adorsys.opba.protocol.xs2a.config",
        "de.adorsys.opba.protocol.xs2a.service",
        "de.adorsys.opba.protocol.xs2a.entrypoint"
})
public class Xs2aProtocolConfiguration {
}
