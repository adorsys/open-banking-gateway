package de.adorsys.opba.protocol.api.services.scoped.validation;

/**
 * Validation rule that enforces all necessary parameters are present to call ASPSP/Bank API.
 */
public interface ValidationRule {

    /**
     * @return If the rule applies to the current context.
     */
    boolean applies();
}
