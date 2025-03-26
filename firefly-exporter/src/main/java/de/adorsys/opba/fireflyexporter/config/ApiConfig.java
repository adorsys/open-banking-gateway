package de.adorsys.opba.fireflyexporter.config;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@Data
@Validated
@Configuration
@ConfigurationProperties("api")
public class ApiConfig {

    @NotNull
    private URI url;

    @NotBlank
    private String redirectOkTemplate;

    @NotBlank
    private String redirectNokTemplate;

    public String getRedirectOkUri(String redirectCode) {
        return UriComponentsBuilder.fromHttpUrl(redirectOkTemplate)
                .buildAndExpand(ImmutableMap.of("redirectCode", redirectCode))
                .toUri()
                .toASCIIString();


    }

    public String getRedirectNokUri() {
        return UriComponentsBuilder.fromHttpUrl(redirectNokTemplate)
                .build()
                .toUri()
                .toASCIIString();
    }
}
