package de.adorsys.opba.protocol.xs2a.entrypoint.pis;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraAuthRequestParam;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.PaymentProductDetails;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.pis.SinglePayment;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.context.pis.SinglePaymentXs2aContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aOutcomeMapper;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aResultBodyExtractor;
import de.adorsys.opba.protocol.xs2a.entrypoint.helpers.Xs2aUuidMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_REQUEST_SAGA;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Entry point that handles payment initiation request from the FinTech. Prepares the context and triggers BPMN engine for
 * further actions.
 */
@Service("xs2aInitiateSinglePayment")
@RequiredArgsConstructor
public class Xs2aInitiateSinglePaymentEntrypoint implements SinglePayment {

    private final RuntimeService runtimeService;
    private final Xs2aResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final Xs2aInitiateSinglePaymentEntrypoint.FromRequest mapper;
    private final ExtendWithServiceContext extender;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    @Override
    public CompletableFuture<Result<SinglePaymentBody>> execute(ServiceContext<InitiateSinglePaymentRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                XS2A_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, prepareContext(serviceContext)))
        );

        CompletableFuture<Result<SinglePaymentBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new Xs2aOutcomeMapper<>(result, extractor::extractSinglePaymentBody, errorMapper)
        );
        return result;
    }

    protected SinglePaymentXs2aContext prepareContext(ServiceContext<InitiateSinglePaymentRequest> serviceContext) {
        InitiateSinglePaymentRequest request = serviceContext.getRequest();

        SinglePaymentXs2aContext context = mapper.map(request);
        context.setAction(ProtocolAction.SINGLE_PAYMENT);
        extender.extend(context, serviceContext);

        Optional<String> psuIdOptional = Optional.ofNullable(request.getExtras())
                                                 .map(ex -> ex.get(ExtraAuthRequestParam.PSU_ID))
                                                 .map(Object::toString);

        psuIdOptional.ifPresent(context::setPsuId);

        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = Xs2aUuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<InitiateSinglePaymentRequest, SinglePaymentXs2aContext> {

        @Mapping(source = "facadeServiceable.bankProfileId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        @Mapping(source = "facadeServiceable.tppBrandLoggingInformation", target = "tppBrandLoggingInformation")
        @Mapping(source = "facadeServiceable.tppNotificationURI", target = "tppNotificationURI")
        @Mapping(source = "facadeServiceable.tppNotificationContentPreferred", target = "tppNotificationContentPreferred")
        @Mapping(source = "facadeServiceable.tppDecoupledPreferred", target = "tppDecoupledPreferred")
        @Mapping(source = "facadeServiceable.uaContext.psuAccept", target = "contentType", nullValuePropertyMappingStrategy = IGNORE)
        @Mapping(source = "singlePayment", target = "payment", nullValuePropertyMappingStrategy = IGNORE)
        @Mapping(source = "singlePayment.paymentId", target = "paymentId")
        @Mapping(expression = "java(mapStandardPaymentProductToString(ctx.getSinglePayment().getPaymentProduct()))", target = "paymentProduct")
        @Mapping(source = "singlePayment.creditorAddress", target = "payment.creditorAddress")
        SinglePaymentXs2aContext map(InitiateSinglePaymentRequest ctx);

        default String mapStandardPaymentProductToString(PaymentProductDetails from) {
            if (null == from) {
                return null;
            }

            return from.toString();
        }
    }
}
