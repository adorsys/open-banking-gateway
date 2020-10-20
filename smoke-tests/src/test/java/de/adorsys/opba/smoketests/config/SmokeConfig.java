package de.adorsys.opba.smoketests.config;

import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class SmokeConfig {

    public static final LocalDate DATE_FROM = LocalDate.now().minusYears(1);
    public static final LocalDate DATE_TO = LocalDate.now();
    public static final String BOTH_BOOKING = "BOTH";

    @Getter
    @Value("${test.smoke.opba.server-uri}")
    private String opbaServerUri;

    // For non-browser tests. Cookie must reside on opba-ui domain if requests are proxied through UI
    @Getter
    @Value("${test.smoke.opba.ui-uri}")
    private String uiUri;

    @Getter
    @Value("${test.smoke.aspsp-profile.server-uri}")
    private String aspspProfileServerUri;

    @Getter
    @Value("${test.smoke.tpp.server-uri}")
    private String sandboxTppManagementServerUrl;

    @Getter
    @Value("${test.smoke.tpp.management.username}")
    private String sandboxTppManagementUserName;

    @Getter
    @Value("${test.smoke.tpp.management.password}")
    private String sandboxTppManagementPassword;

    @MockBean
    // Stubbing out as they are not available, but currently breaking hierarchy has no sense as we can replace this with REST in future
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private BankProfileJpaRepository profiles;

    @MockBean
    // Stubbing out as they are not available, but currently breaking hierarchy has no sense as we can replace this with REST in future
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private ConsentRepository consents;

    @Bean
    SandboxConsentAuthApproachState authState() {
        return new SandboxConsentAuthApproachState(aspspProfileServerUri);
    }

    @Bean
    @ConfigurationProperties(prefix = "test.api.cookie")
    CookieProperties properties() {
        return new CookieProperties();
    }
}
