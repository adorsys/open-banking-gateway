package de.adorsys.opba.protocol.xs2a.service.xs2a.validation;

import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidAisConsent;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AisConsentValidator implements ConstraintValidator<ValidAisConsent, String> {
    private AisConsentInitiateBody.AccountAccessBody body;

    @Override
    public void initialize(ValidAisConsent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!isValidDedicatedConsent(body)) {
            return false;
        }

        if (!isValidGlobalConsent(body)) {
            return false;
        }

        if (!isValidBankOfferedConsent(body)) {
            return false;
        }
        return true;
    }

    private boolean isValidDedicatedConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return false;
    }

    private boolean isValidGlobalConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return false;
    }

    private boolean isValidBankOfferedConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return false;
    }
}
