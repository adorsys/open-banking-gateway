package de.adorsys.opba.adminapi.config;

import de.adorsys.opba.api.security.internal.EnableSignatureBasedApiSecurity;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

import static de.adorsys.opba.adminapi.config.Const.DISABLED_ON_NO_ADMIN_API;
import static de.adorsys.opba.tppbankingapi.config.ConfigConst.BANKING_API_CONFIG_PREFIX;

@Data
@Validated
@EnableSignatureBasedApiSecurity
@Profile(DISABLED_ON_NO_ADMIN_API)
@ConfigurationProperties(prefix = BANKING_API_CONFIG_PREFIX + "admin-api.security")
public class AdminApiSecurityFilterConfig {

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotEmpty
    private Set<@NotBlank String> urlPatternsToProtect;

    @Bean
    @Profile("!no-admin-api-security")
    public FilterRegistrationBean<AdminApiSecurityFilter> adminApiSecurity() {
        FilterRegistrationBean<AdminApiSecurityFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AdminApiSecurityFilter(login, password));
        registrationBean.setUrlPatterns(urlPatternsToProtect);

        return registrationBean;
    }
}
