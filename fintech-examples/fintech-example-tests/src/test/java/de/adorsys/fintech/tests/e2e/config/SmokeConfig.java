package de.adorsys.fintech.tests.e2e.config;

import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmokeConfig {

    @Getter
    @Value("${test.fintech.uri}")
    private String fintechServerUri;

    @Getter
    @Value("${test.aspsp-profile.server-uri}")
    private String aspspProfileServerUri;

    @Getter
    @Value("${test.opba.server-uri}")
    private String opbaServerUri;


    @Getter
    @Value("${test.fintech.search.uri}")
    private String fintechSearchUri;

    @Getter
    @Value("${test.tpp.server-uri}")
    private String sandboxTppManagementServerUrl;

    @Getter
    @Value("${test.tpp.management.username}")
    private String sandboxTppManagementUserName;

    @Getter
    @Value("${test.tpp.management.password}")
    private String sandboxTppManagementPassword;

    @MockBean
    // Stubbing out as they are not available, but currently breaking hierarchy has no sense as we can replace this with REST in future
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private BankProfileJpaRepository profiles;

    @MockBean // Stubbing out as they are not available, but currently breaking hierarchy has no sense as we can replace this with REST in future
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private ConsentRepository consents;

    @Bean
    ConsentAuthApproachState authState() {
        return new ConsentAuthApproachState(aspspProfileServerUri);
    }
}
