package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ResponseTokenMapper;
import de.adorsys.xs2a.adapter.api.RequestHeaders;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Object that represents request Headers that are necessary to call ASPSP API for consent initiation.
 */
@SuppressWarnings("PMD")
@Getter
@Setter
public class ConsentInitiateV139Headers extends ConsentInitiateHeaders {

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
    public interface FromAisCtx extends DtoMapper<Xs2aAisContext, ConsentInitiateV139Headers> {

        ConsentInitiateV139Headers map(Xs2aAisContext ctx);
    }

}
