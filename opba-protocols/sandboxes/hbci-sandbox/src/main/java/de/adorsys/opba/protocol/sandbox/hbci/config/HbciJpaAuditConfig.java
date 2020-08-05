package de.adorsys.opba.protocol.sandbox.hbci.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@ConditionalOnMissingBean(AuditingEntityListener.class)
public class HbciJpaAuditConfig {
}
