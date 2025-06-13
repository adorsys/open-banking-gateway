package de.adorsys.opba.db.domain.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@SuperBuilder
@DiscriminatorValue(IgnoreValidationRule.MODE_IGNORE)
@NoArgsConstructor
public class IgnoreValidationRule extends ValidationRule {
    public static final String MODE_IGNORE = "IGNORE";
}
