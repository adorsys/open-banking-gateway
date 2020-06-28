package de.adorsys.opba.protocol.sandbox.hbci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class HbciServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HbciServerApplication.class, args);
    }
}
