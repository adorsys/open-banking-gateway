package de.adorsys.opba.db.domain.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("IGNORE")
public class IgnoreBankValidationRule extends BankValidationRule {
}
