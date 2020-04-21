package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.BankValidationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankValidationRuleRepository extends JpaRepository<BankValidationRule, Long> {
    List<BankValidationRule> findByProtocolId(Long bankProtocolId);
}
