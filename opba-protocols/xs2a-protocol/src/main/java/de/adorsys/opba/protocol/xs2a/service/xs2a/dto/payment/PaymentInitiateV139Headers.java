package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ResponseTokenMapper;
import de.adorsys.xs2a.adapter.api.RequestHeaders;
import lombok.Data;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Object that represents request Headers that are necessary to call ASPSP API Version 1.3.9 for consent initiation.
 */
@Data
@SuppressWarnings("PMD")
public class PaymentInitiateV139Headers extends PaymentInitiateHeaders {
    /**
     * This header might be used by Fintechs to inform the ASPSP about the brand used by the Fintech towards the PSU.
     */
    private  String fintechBrandLoggingInformation;

    /**
     *  URI for the Endpoint of the Fintech-API to which the status of the payment initiation should be sent.
     */
    private  String fintechNotificationURI;

    /**
     * The string has the formstatus=X1, ..., Xn where Xi is one of the constants SCA, PROCESS, LAST and where constants are not repeated.
     */
    private  String fintechNotificationContentPreferred;

    /**
     * If it equals "true", the Fintech prefers a decoupled SCA approach
     */
    private  boolean fintechDecoupledPreferred;


    @Override
    public RequestHeaders toHeaders() {
        //TODO: add new Headers when they will be available as constant in RequestHeaders class
        return super.toHeaders();
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = ResponseTokenMapper.class)
    public interface FromPisCtx extends DtoMapper<Xs2aPisContext, PaymentInitiateV139Headers> {
        PaymentInitiateV139Headers map(Xs2aPisContext ctx);
    }


}
