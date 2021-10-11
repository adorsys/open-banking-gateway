package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import org.apache.logging.log4j.util.Strings;
import org.kapott.hbci.manager.HBCIUtils;
import org.springframework.stereotype.Service;

/**
 * Updates/extends already context with incoming service request by filling request fields. Also updates bank adapter URL
 * if needed.
 */
@Service
public class HbciExtendWithServiceContext {

    public HbciContext extend(HbciContext context, ServiceContext<?> serviceContext) {
        context.setRequestScoped(serviceContext.getRequestScoped());
        context.setServiceSessionId(serviceContext.getServiceSessionId());
        context.setRedirectCodeIfAuthContinued(serviceContext.getFutureRedirectCode().toString());
        context.setAspspRedirectCode(serviceContext.getFutureAspspRedirectCode().toString());
        context.setAuthorizationSessionIdIfOpened(serviceContext.getFutureAuthSessionId().toString());
        setBankApiUrlIfNeeded(serviceContext);

        return context;
    }

    private void setBankApiUrlIfNeeded(ServiceContext<?> serviceContext) {
        var url = serviceContext.getRequestScoped().aspspProfile().getUrl();
        if (Strings.isBlank(url)) {
            return;
        }

        HBCIUtils.getBankInfo(serviceContext.getRequestScoped().aspspProfile().getBankCode()).setPinTanAddress(url);
    }
}
