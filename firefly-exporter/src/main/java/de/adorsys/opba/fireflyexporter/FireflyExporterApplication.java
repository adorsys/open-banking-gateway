package de.adorsys.opba.fireflyexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EntityScan(basePackages = "de.adorsys.opba.fireflyexporter.entity")
@EnableJpaRepositories(basePackages = "de.adorsys.opba.fireflyexporter.repository")
@EnableFeignClients(basePackages = "de.adorsys.opba.fireflyexporter.client")
@EnableConfigurationProperties
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class FireflyExporterApplication {

   public static void main(String[] args) {
      SpringApplication.run(FireflyExporterApplication.class, args);
   }
}
