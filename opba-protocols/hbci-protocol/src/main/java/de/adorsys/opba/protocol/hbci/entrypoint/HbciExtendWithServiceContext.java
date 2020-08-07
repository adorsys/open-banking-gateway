package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import org.kapott.hbci.manager.HBCIUtils;
import org.springframework.stereotype.Service;

/**
 * Updates/extends already context with incoming service request by filling request fields. Also updates bank adapter URL
 * if needed.
 */
@Service
public class HbciExtendWithServiceContext {

    public HbciContext extend(HbciContext context, ServiceContext serviceContext) {
        context.setRequestScoped(serviceContext.getRequestScoped());
        context.setServiceSessionId(serviceContext.getServiceSessionId());
        context.setRedirectCodeIfAuthContinued(serviceContext.getFutureRedirectCode().toString());
        context.setAspspRedirectCode(serviceContext.getFutureAspspRedirectCode().toString());
        context.setAuthorizationSessionIdIfOpened(serviceContext.getFutureAuthSessionId().toString());

        HBCIUtils.getBankInfo(serviceContext.getRequestScoped().aspspProfile().getBankCode())
                .setPinTanAddress(serviceContext.getRequestScoped().aspspProfile().getUrl());
        return context;
    }
}
