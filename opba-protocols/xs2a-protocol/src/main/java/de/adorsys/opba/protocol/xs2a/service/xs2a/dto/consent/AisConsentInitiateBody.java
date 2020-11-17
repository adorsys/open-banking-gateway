package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidConsentBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.FREQUENCY_PER_DAY;
import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.IBAN;
import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.RECURRING_INDICATOR;
import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.VALID_UNTIL;
import static de.adorsys.opba.protocol.api.dto.codes.ScopeObject.AIS_CONSENT;
import static de.adorsys.opba.protocol.api.dto.codes.ScopeObject.AIS_CONSENT_SCOPE;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.BOOLEAN;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.DATE;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.INTEGER;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.OBJECT;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AccountAccessType.ALL_ACCOUNTS;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AccountAccessType.ALL_ACCOUNTS_WITH_BALANCES;

/**
 * AIS consent access scope object used to represent Global,Dedicated,etc. consents for
 * AIS (Account Information Services).
 */
@Getter
@Setter
public class AisConsentInitiateBody {

    /**
     * AIS access scope object.
     */
    @Valid
    @ValidationInfo(ui = @FrontendCode(OBJECT), ctx = @ContextCode(target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.accountaccess}")
    @ValidConsentBody
    private AccountAccessBody access;

    /**
     * Is the consent for recurring access.
     */
    @ValidationInfo(ui = @FrontendCode(BOOLEAN), ctx = @ContextCode(value = RECURRING_INDICATOR, target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.recurringIndicator}")
    private Boolean recurringIndicator;

    /**
     * Consent will be valid until.
     */
    @ValidationInfo(ui = @FrontendCode(DATE), ctx = @ContextCode(value = VALID_UNTIL, target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.validUntil}")
    @FutureOrPresent(message = "{future.ctx.validUntil}")
    private LocalDate validUntil;

    /**
     * Consent can be used this times per day.
     */
    @ValidationInfo(ui = @FrontendCode(INTEGER), ctx = @ContextCode(value = FREQUENCY_PER_DAY, target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.frequencyPerDay}")
    private Integer frequencyPerDay;

    // Optional
    /**
     * Consent is for service combination.
     */
    private Boolean combinedServiceIndicator;

    @Getter
    @Setter
    // FIXME: should use conditional validator - access accounts/balances/transactions OR availableAccounts/allPsd2
    public static class AccountAccessBody {

        // These fields are conditionally-validated
        /**
         * Dedicated consent - accounts.
         */
        private List<@Valid AccountReferenceBody> accounts;

        /**
         * Dedicated consent - balances.
         */
        private List<@Valid AccountReferenceBody> balances;

        /**
         * Dedicated consent - transactions.
         */
        private List<@Valid AccountReferenceBody> transactions;

        /**
         * Global consent - allAccounts/allAccountsWithBalances.
         */
        private String availableAccounts;

        /**
         * Global consent - allAccounts (and all transactions, balances).
         */
        private String allPsd2;
    }

    @Getter
    @Setter
    public static class AccountReferenceBody {

        /**
         * Dedicated consent - account IBAN.
         */
        @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(value = IBAN, target = AIS_CONSENT_SCOPE))
        @NotBlank(message = "{no.ctx.iban}")
        private String iban;

        private String bban;
        private String pan;
        private String maskedPan;
        private String msisdn;

        /**
         * Dedicated consent - account currency.
         */
        private String currency;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<AisConsentInitiateBody, Consents> {

        default Consents map(Xs2aAisContext cons) {
            return map(cons.getAisConsent());
        }

        default AccountAccess.AvailableAccountsEnum accounts(String availableAccounts) {
            if (ALL_ACCOUNTS.getApiName().equals(availableAccounts)) {
                return AccountAccess.AvailableAccountsEnum.ALLACCOUNTS;
            }

            return null;
        }

        default AccountAccess.AvailableAccountsWithBalance accountsWithBalance(String availableAccounts) {
            if (ALL_ACCOUNTS_WITH_BALANCES.getApiName().equals(availableAccounts)) {
                return AccountAccess.AvailableAccountsWithBalance.ALLACCOUNTS;
            }

            return null;
        }

        default AccountAccess.AllPsd2Enum allPsd2(String allPsd2) {
            if (ALL_ACCOUNTS.getApiName().equals(allPsd2)) {
                return AccountAccess.AllPsd2Enum.ALLACCOUNTS;
            }

            return null;
        }

        @Mapping(source = "cons.access.availableAccounts", target = "access.availableAccounts")
        @Mapping(source = "cons.access.availableAccounts", target = "access.availableAccountsWithBalance")
        @Mapping(source = "cons.access.allPsd2", target = "access.allPsd2")
        Consents map(AisConsentInitiateBody cons);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aAisContext, AisConsentInitiateBody> {

        default AisConsentInitiateBody map(Xs2aAisContext cons) {
            return null == cons.getAisConsent() ? new AisConsentInitiateBody() : cons.getAisConsent();
        }
    }
}
