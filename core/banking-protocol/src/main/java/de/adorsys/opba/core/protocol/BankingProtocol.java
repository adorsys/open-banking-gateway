package de.adorsys.opba.core.protocol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// FIXME - Multiple DataSource requires JTA
@EnableConfigurationProperties
@EnableTransactionManagement
@SpringBootApplication(
        scanBasePackages = {
                "de.adorsys.opba.core.protocol.config",
                "de.adorsys.opba.core.protocol.controller",
                "de.adorsys.opba.core.protocol.service"
        },
        // For Atomikos:
        exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class
        }
)
public class BankingProtocol {

    public static void main(String[] args) {
        SpringApplication.run(BankingProtocol.class, args);
    }
}
