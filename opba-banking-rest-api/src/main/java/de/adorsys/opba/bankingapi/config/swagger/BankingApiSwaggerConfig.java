package de.adorsys.opba.bankingapi.config.swagger;

import com.google.common.base.Predicates;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableSwagger2
public class BankingApiSwaggerConfig {
    private static final String DEFAULT_BANKING_API_LOCATION = "/banking_api_ais.yml";

    @Value("${opba.swagger.banking.api.location:}")
    private String customBankingApiLocation;

    // Intellij IDEA claims that Guava predicates could be replaced with Java API,
    // but actually it is not possible
    @SuppressWarnings("Guava")
    @Bean(name = "api")
    public Docket apiDocklet() {
        return new Docket(DocumentationType.SWAGGER_2)
                       .apiInfo(new ApiInfoBuilder().build())
                       .select()
                       .paths(Predicates.not(PathSelectors.regex("/error.*?")))
                       .paths(Predicates.not(PathSelectors.regex("/connect.*")))
                       .paths(Predicates.not(PathSelectors.regex("/management.*")))
                       .build();
    }

    @Bean
    @Primary
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setLocation(resolveYamlLocation());
            return Collections.singletonList(swaggerResource);
        };
    }

    private String resolveYamlLocation() {
        if (StringUtils.isBlank(customBankingApiLocation)) {
            return DEFAULT_BANKING_API_LOCATION;
        }
        return customBankingApiLocation;
    }
}
