package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.service.model.SinglePaymentInitiationBody;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static de.adorsys.opba.protocol.api.dto.codes.FieldCode.IBAN;
import static de.adorsys.opba.protocol.api.dto.codes.ScopeObject.PIS_CONSENT;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.OBJECT;
import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Payment request body (PIS)
 */
@Getter
@Setter
public class PaymentInitiateBody {

    @Valid
    @ValidationInfo(ui = @FrontendCode(OBJECT), ctx = @ContextCode(target = PIS_CONSENT))
    @NotNull(message = "{no.ctx.debtorAccount}")
    //@ValidPaymentBody
    private AccountReferenceBody debtorAccount;

    @Valid
    @ValidationInfo(ui = @FrontendCode(OBJECT), ctx = @ContextCode(target = PIS_CONSENT))
    @NotNull(message = "{no.ctx.instructedAmount}")
    //@ValidPaymentBody
    private AmountBody instructedAmount;

    @Valid
    @ValidationInfo(ui = @FrontendCode(OBJECT), ctx = @ContextCode(target = PIS_CONSENT))
    @NotNull(message = "{no.ctx.creditorAccount}")
    //@ValidPaymentBody
    private AccountReferenceBody creditorAccount;

    private String creditorName;
    // optional
    private String creditorAgent;

    @Valid
    @ValidationInfo(ui = @FrontendCode(OBJECT), ctx = @ContextCode(target = PIS_CONSENT))
    @NotNull(message = "{no.ctx.creditorAddress}")
    //@ValidPaymentBody
    private AddressBody creditorAddress;
    private String remittanceInformationUnstructured;
    private String endToEndIdentification;

    @Getter
    @Setter
    public static class AccountReferenceBody {
        @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(value = IBAN, target = PIS_CONSENT))
        @NotBlank(message = "{no.ctx.iban}")
        private String iban;
        private String bban;
        private String pan;
        private String maskedPan;
        private String msisdn;
        private String currency;
    }

    @Getter
    @Setter
    public static class AmountBody {
        private String currency;
        private String amount;
    }

    @Getter
    @Setter
    public static class AddressBody {
        private String streetName;
        private String buildingNumber;
        private String city;
        private String postCode;
        private String country;
    }


    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface ToXs2aApi extends DtoMapper<PaymentInitiateBody, SinglePaymentInitiationBody> {

        default SinglePaymentInitiationBody map(Xs2aPisContext cons) {
            return map(cons.getPayment());
        }

        SinglePaymentInitiationBody map(PaymentInitiateBody cons);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aPisContext, PaymentInitiateBody> {

        default PaymentInitiateBody map(Xs2aPisContext cons) {
            return null == cons.getPayment() ? new PaymentInitiateBody() : cons.getPayment();
        }
    }
}
