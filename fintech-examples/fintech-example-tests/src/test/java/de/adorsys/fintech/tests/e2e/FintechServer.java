package de.adorsys.fintech.tests.e2e;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFinTechImplConfig
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class FintechServer {
}
