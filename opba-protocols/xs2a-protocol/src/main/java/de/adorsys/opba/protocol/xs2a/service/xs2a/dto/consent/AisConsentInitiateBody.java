package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
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

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.TargetObject.AIS_CONSENT;

@Getter
@Setter
public class AisConsentInitiateBody {

    @Valid
    @ValidationInfo(ui = @FrontendCode("accountaccess.class"), ctx = @ContextCode(value = "access", target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.accountaccess}")
    private AccountAccessBody access;

    @ValidationInfo(ui = @FrontendCode("boolean.boolean"), ctx = @ContextCode(value = "recurringIndicator", target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.recurringIndicator}")
    private Boolean recurringIndicator;

    @ValidationInfo(ui = @FrontendCode("date.string"), ctx = @ContextCode(value = "validUntil", target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.validUntil}")
    @FutureOrPresent(message = "{future.ctx.validUntil}")
    private LocalDate validUntil;

    @ValidationInfo(ui = @FrontendCode("textbox.integer"), ctx = @ContextCode(value = "frequencyPerDay", target = AIS_CONSENT))
    @NotNull(message = "{no.ctx.frequencyPerDay}")
    private Integer frequencyPerDay;

    // Optional
    private Boolean combinedServiceIndicator;

    @Getter
    @Setter
    // FIXME: should use conditional validator - access accounts/balances/transactions OR availableAccounts/allPsd2
    public static class AccountAccessBody {

        // These fields are conditionally-validated
        private List<@Valid AccountReferenceBody> accounts;
        private List<@Valid AccountReferenceBody> balances;
        private List<@Valid AccountReferenceBody> transactions;

        private String availableAccounts;
        private String allPsd2;
    }

    @Getter
    @Setter
    public static class AccountReferenceBody {

        @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode(prefix = "ais", target = AIS_CONSENT))
        @NotBlank(message = "{no.ctx.iban}")
        private String iban;

        private String bban;
        private String pan;
        private String maskedPan;
        private String msisdn;
        private String currency;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<AisConsentInitiateBody, Consents> {

        default Consents map(Xs2aAisContext cons) {
            return map(cons.getAisConsent());
        }

        default AccountAccess.AvailableAccountsEnum accounts(String availableAccounts) {
            if ("ALL_ACCOUNTS".equals(availableAccounts)) {
                return AccountAccess.AvailableAccountsEnum.ALLACCOUNTS;
            }

            return null;
        }

        default AccountAccess.AvailableAccountsWithBalance accountsWithBalance(String availableAccounts) {
            if ("ALL_ACCOUNTS_WITH_BALANCES".equals(availableAccounts)) {
                return AccountAccess.AvailableAccountsWithBalance.ALLACCOUNTS;
            }

            return null;
        }

        @Mapping(source = "cons.access.availableAccounts", target = "access.availableAccounts")
        @Mapping(source = "cons.access.availableAccounts", target = "access.availableAccountsWithBalance")
        Consents map(AisConsentInitiateBody cons);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aAisContext, AisConsentInitiateBody> {

        default AisConsentInitiateBody map(Xs2aAisContext cons) {
            return null == cons.getAisConsent() ? new AisConsentInitiateBody() : cons.getAisConsent();
        }
    }
}
