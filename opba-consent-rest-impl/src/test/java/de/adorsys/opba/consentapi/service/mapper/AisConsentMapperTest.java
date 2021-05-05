package de.adorsys.opba.consentapi.service.mapper;

import de.adorsys.opba.consentapi.model.generated.AisAccountAccessInfo;
import de.adorsys.opba.consentapi.model.generated.AisConsentRequest;
import de.adorsys.opba.consentapi.model.generated.ConsentAuth;
import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static de.adorsys.opba.restapi.shared.GlobalConst.CONSENT_MAPPERS_PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AisConsentMapperTest.TestConfiguration.class)
class AisConsentMapperTest {

    @Autowired
    private AisConsentMapper mapper;

    @Test
    void testMapAisConsentWorks() {
        var request = new PsuAuthRequest();
        var consentAuth = new ConsentAuth();
        var consent = new AisConsentRequest();
        var access = new AisAccountAccessInfo();
        request.setConsentAuth(consentAuth);
        consentAuth.setConsent(consent);
        consent.setAccess(access);
        consent.setCombinedServiceIndicator(true);
        consent.setFrequencyPerDay(99);
        consent.setRecurringIndicator(true);
        consent.setValidUntil(LocalDate.MAX);

        mapper.map(request);

        assertThat(request.getConsentAuth().getConsent().getAccess()).isEqualTo(access);
        assertThat(request.getConsentAuth().getConsent().isCombinedServiceIndicator()).isEqualTo(true);
        assertThat(request.getConsentAuth().getConsent().getFrequencyPerDay()).isEqualTo(99);
        assertThat(request.getConsentAuth().getConsent().isRecurringIndicator()).isEqualTo(true);
        assertThat(request.getConsentAuth().getConsent().getValidUntil()).isEqualTo(LocalDate.MAX);
    }

    @Configuration
    @ComponentScan(basePackages = CONSENT_MAPPERS_PACKAGE)
    public static class TestConfiguration {
    }
}