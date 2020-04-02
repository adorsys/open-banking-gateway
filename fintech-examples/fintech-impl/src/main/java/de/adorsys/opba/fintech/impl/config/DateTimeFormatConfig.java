package de.adorsys.opba.fintech.impl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
class DateTimeFormatConfig implements WebMvcConfigurer {

    /**
     * Swagger-codegen is not able to produce @DateTimeFormat annotation:
     * https://github.com/swagger-api/swagger-codegen/issues/1235
     * https://github.com/swagger-api/swagger-codegen/issues/4113
     * To fix this - forcing formatters globally.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }
}

