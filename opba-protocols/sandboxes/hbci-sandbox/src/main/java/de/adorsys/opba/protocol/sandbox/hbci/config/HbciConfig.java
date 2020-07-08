package de.adorsys.opba.protocol.sandbox.hbci.config;

import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "hbci")
public class HbciConfig {

    @NotEmpty
    private List<@NotNull Bank> banks;

    @NotEmpty
    private List<@NotNull User> users;
}
