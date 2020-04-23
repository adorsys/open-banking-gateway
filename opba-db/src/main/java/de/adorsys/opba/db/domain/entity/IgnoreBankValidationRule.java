package de.adorsys.opba.db.domain.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@SuperBuilder
@DiscriminatorValue("IGNORE")
@NoArgsConstructor
public class IgnoreBankValidationRule extends BankValidationRule {
}
