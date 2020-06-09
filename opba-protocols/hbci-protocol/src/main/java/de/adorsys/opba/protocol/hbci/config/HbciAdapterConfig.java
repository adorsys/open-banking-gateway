package de.adorsys.opba.protocol.hbci.config;

import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.hbci.HbciBanking;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.Resources;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIProduct;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class HbciAdapterConfig {

    private final HbciAdapterProperties properties;

    @Bean
    @SneakyThrows
    OnlineBankingService onlineBankingService(HBCIProduct product) {
        try (InputStream is = Resources.getInputStream("blz.properties")) {
            HBCIUtils.refreshBLZList(is);
        }

        OnlineBankingService hbci = new HbciBanking(
                product,
                properties.getSysIdExpirationTimeMs(),
                properties.getUpdExpirationTimeMs()
        );

        // Initiate MOCK bank, should come after HbciBanking is created
        BankInfo bankInfo = new BankInfo();
        bankInfo.setBlz("10000001");
        bankInfo.setPinTanAddress("http://localhost:8090/hbci-mock/");
        bankInfo.setPinTanVersion(HBCIVersion.byId(properties.getHbciVersion()));
        bankInfo.setBic("ADORSYS");
        HBCIUtils.addBankInfo(bankInfo);

        return hbci;
    }

    @Bean
    HBCIProduct product() {
        return new HBCIProduct(properties.getHbciProduct(), properties.getHbciVersion());
    }
}
