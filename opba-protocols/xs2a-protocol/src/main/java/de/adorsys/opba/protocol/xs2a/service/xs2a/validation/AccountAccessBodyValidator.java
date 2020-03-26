package de.adorsys.opba.protocol.xs2a.service.xs2a.validation;

import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidConsentBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAccessBodyValidator implements ConstraintValidator<ValidConsentBody, AisConsentInitiateBody.AccountAccessBody> {

    @Override
    public void initialize(ValidConsentBody constraintAnnotation) {
    }

    @Override
    public boolean isValid(AisConsentInitiateBody.AccountAccessBody body, ConstraintValidatorContext context) {
        if (body == null) {
            return true;
        }

        return isValidAisConsentStructure(body);
    }

    private boolean isValidAisConsentStructure(AisConsentInitiateBody.AccountAccessBody body) {
        return isValidDedicatedConsent(body) || isValidGlobalConsent(body) || isValidBankOfferedConsent(body);
    }

    private boolean isValidDedicatedConsent(AisConsentInitiateBody.AccountAccessBody body) {
        boolean validDedicatedWithAccounts = !isEmptyAccountInfo(body)
                                                     && StringUtils.isEmpty(body.getAllPsd2())
                                                     && StringUtils.isEmpty(body.getAvailableAccounts());

        boolean validDedicatedWithoutAccounts = isEmptyAccountInfo(body)
                                                        && StringUtils.isEmpty(body.getAllPsd2())
                                                        && "allAccounts".equals(body.getAvailableAccounts());

        return validDedicatedWithAccounts || validDedicatedWithoutAccounts;
    }

    private boolean isValidGlobalConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return isEmptyAccountInfo(body)
                       && "allAccounts".equals(body.getAllPsd2())
                       && StringUtils.isEmpty(body.getAvailableAccounts());
    }

    private boolean isValidBankOfferedConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return isEmptyAccountInfo(body)
                       && StringUtils.isEmpty(body.getAllPsd2())
                       && StringUtils.isEmpty(body.getAvailableAccounts());
    }

    private boolean isEmptyAccountInfo(AisConsentInitiateBody.AccountAccessBody body) {
        return CollectionUtils.isEmpty(body.getAccounts())
                       && CollectionUtils.isEmpty(body.getBalances())
                       && CollectionUtils.isEmpty(body.getTransactions());
    }
}
