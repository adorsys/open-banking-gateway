package de.adorsys.opba.helpers.protocol.testing;

import de.adorsys.opba.protocol.hbci.EnableHbciProtocol;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import de.adorsys.opba.protocol.xs2a.EnableXs2aSandboxProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableHbciProtocol
@EnableXs2aProtocol
@EnableXs2aSandboxProtocol
@SpringBootApplication
@EnableConfigurationProperties
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class OpenBankingTestingHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenBankingTestingHelperApplication.class, args);
    }
}
