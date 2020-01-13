package de.adorsys.opba.core.protocol.service.xs2a.dto.consent;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.model.AccountAccess;
import de.adorsys.xs2a.adapter.service.model.Consents;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ConsentsBody {

    public static final ToXs2aApi TO_XS2A = Mappers.getMapper(ToXs2aApi.class);
    public static final FromCtx FROM_CTX = Mappers.getMapper(FromCtx.class);

    @Valid
    @ValidationInfo(ui = @FrontendCode("accountaccess.class"), ctx = @ContextCode("consent.access"))
    @NotNull(message = "{no.ctx.accountaccess}")
    private AccountAccessBody access;

    @ValidationInfo(ui = @FrontendCode("boolean.boolean"), ctx = @ContextCode("consent.recurringIndicator"))
    @NotNull(message = "{no.ctx.recurringIndicator}")
    private Boolean recurringIndicator;

    @ValidationInfo(ui = @FrontendCode("date.string"), ctx = @ContextCode("consent.validUntil"))
    @NotNull(message = "{no.ctx.validUntil}")
    private LocalDate validUntil;

    @ValidationInfo(ui = @FrontendCode("textbox.integer"), ctx = @ContextCode("consent.frequencyPerDay"))
    @NotNull(message = "{no.ctx.frequencyPerDay}")
    private Integer frequencyPerDay;

    // Optional
    private Boolean combinedServiceIndicator;

    @Getter
    @Setter
    public static class AccountAccessBody {

        // These fields are conditionally-validated
        private List<@Valid AccountReferenceBody> accounts;
        private List<@Valid AccountReferenceBody> balances;
        private List<@Valid AccountReferenceBody> transactions;

        private AccountAccess.AvailableAccountsEnum availableAccounts;
        private AccountAccess.AllPsd2Enum allPsd2;

        @Mapper
        public interface ToXs2aApi {
            Consents map(AccountAccessBody cons);
        }
    }

    @Getter
    @Setter
    public static class AccountReferenceBody {

        @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode(prefix = "consent"))
        @NotBlank(message = "{no.ctx.iban}")
        private String iban;

        private String bban;
        private String pan;
        private String maskedPan;
        private String msisdn;

        // TODO check if it is necessary
        @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode(prefix = "consent"))
        @NotBlank(message = "{no.ctx.currency}")
        private String currency;

        @Mapper
        public interface ToXs2aApi {
            AccountAccess map(AccountReferenceBody cons);
        }
    }

    @Mapper
    public interface ToXs2aApi {
        Consents map(ConsentsBody cons);
    }

    @Mapper
    public interface FromCtx {

        default ConsentsBody map(Xs2aContext cons) {
            return null == cons.getConsent() ? new ConsentsBody() : cons.getConsent();
        }
    }
}
