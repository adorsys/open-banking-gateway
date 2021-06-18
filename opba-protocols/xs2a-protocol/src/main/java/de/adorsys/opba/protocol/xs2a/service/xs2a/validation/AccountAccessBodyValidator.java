package de.adorsys.opba.protocol.xs2a.service.xs2a.validation;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidConsentBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AccountAccessType;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Special validator to check that AIS consent scope object {@link AisConsentInitiateBody} can be used to call ASPSP.
 */
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
                                                     && Strings.isNullOrEmpty(body.getAllPsd2())
                                                     && Strings.isNullOrEmpty(body.getAvailableAccounts());

        boolean validDedicatedWithoutAccounts = isEmptyAccountInfo(body)
                                                        && Strings.isNullOrEmpty(body.getAllPsd2())
                                                        && Arrays.stream(AccountAccessType.values()).anyMatch(it -> it.getApiName().equals(body.getAllPsd2()));

        return validDedicatedWithAccounts || validDedicatedWithoutAccounts;
    }

    private boolean isValidGlobalConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return isEmptyAccountInfo(body)
                       && Arrays.stream(AccountAccessType.values()).anyMatch(it -> it.getApiName().equals(body.getAllPsd2()))
                       && Strings.isNullOrEmpty(body.getAvailableAccounts());
    }

    private boolean isValidBankOfferedConsent(AisConsentInitiateBody.AccountAccessBody body) {
        return isEmptyAccountInfo(body)
                       && Strings.isNullOrEmpty(body.getAllPsd2())
                       && Strings.isNullOrEmpty(body.getAvailableAccounts());
    }

    private boolean isEmptyAccountInfo(AisConsentInitiateBody.AccountAccessBody body) {
        return CollectionUtils.isEmpty(body.getAccounts())
                       && CollectionUtils.isEmpty(body.getBalances())
                       && CollectionUtils.isEmpty(body.getTransactions());
    }
}
