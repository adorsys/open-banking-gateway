package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.IgnoreBankValidationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IgnoreBankValidationRuleRepository extends JpaRepository<IgnoreBankValidationRule, Long> {
    List<IgnoreBankValidationRule> findByProtocolId(Long bankProtocolId);
}
