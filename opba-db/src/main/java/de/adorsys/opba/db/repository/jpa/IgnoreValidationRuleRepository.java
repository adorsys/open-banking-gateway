package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.IgnoreValidationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IgnoreValidationRuleRepository extends JpaRepository<IgnoreValidationRule, Long> {
    List<IgnoreValidationRule> findByProtocolId(Long bankProtocolId);
}
