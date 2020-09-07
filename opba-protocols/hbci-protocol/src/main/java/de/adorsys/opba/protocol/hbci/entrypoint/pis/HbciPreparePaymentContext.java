package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import de.adorsys.multibanking.domain.Bank;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.hbci.HbciUuidMapper;
import de.adorsys.opba.protocol.hbci.service.SafeCacheSerDeUtil;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciExtendWithServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

@Component
@RequiredArgsConstructor
public class HbciPreparePaymentContext {
    private final HbciPreparePaymentContext.FromRequest fromRequest;
    private final HbciExtendWithServiceContext extender;
    private final HbciPaymentContextMergeMapper mergeContextMapper;
    private final SafeCacheSerDeUtil safeSerDe;

    @SneakyThrows
    protected PaymentHbciContext prepareContext(ServiceContext<? extends FacadeServiceableGetter> serviceContext, ProtocolAction action) {
        PaymentHbciContext context = fromRequest.map2Context(serviceContext.getRequest());
        context.setAction(action);
        extender.extend(context, serviceContext);

        Bank bank = new Bank();
        bank.setBic(serviceContext.getRequestScoped().aspspProfile().getBic());
        bank.setBankCode(serviceContext.getRequestScoped().aspspProfile().getBankCode());
        context.setBank(bank);

        ProtocolFacingPayment payment = serviceContext.getRequestScoped().paymentAccess().getFirstByCurrentSession();
        PaymentHbciContext savedPaymentContext = (PaymentHbciContext) safeSerDe.safeDeserialize(payment.getPaymentContext());
        PaymentHbciContext mergedContext = mergeContextMapper.merge(context, savedPaymentContext);
        mergedContext.getPayment().setPaymentId(payment.getPaymentId());
        mergedContext.setPsuPin(savedPaymentContext.getHbciDialogConsent().getCredentials().getPin());
        mergedContext.getHbciDialogConsent().setWithHktan(false);
        mergedContext.getHbciDialogConsent().setScaAuthenticationData(savedPaymentContext.getPsuTan());
        return mergedContext;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = HbciUuidMapper.class, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<InitiateSinglePaymentRequest, PaymentHbciContext> {

        @Mapping(source = "facadeServiceable.bankId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        PaymentHbciContext map2Context(FacadeServiceableGetter ctx);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public interface HbciPaymentContextMergeMapper {
        PaymentHbciContext merge(PaymentHbciContext source, @MappingTarget PaymentHbciContext target);
    }
}
