package de.adorsys.opba.fireflyexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"de.adorsys.opba.firefly.api", "de.adorsys.opba.tpp"})
@EnableConfigurationProperties
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class FireflyExporterApplication {

   public static void main(String[] args) {
      SpringApplication.run(FireflyExporterApplication.class, args);
   }
}
