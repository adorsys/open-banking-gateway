package de.adorsys.opba.protocol.hbci.entrypoint.ais;

import de.adorsys.opba.protocol.api.ais.DeleteConsent;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.DeleteConsentRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DeleteConsentBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.hbci.util.logresolver.HbciLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service("hbciDeleteConsent")
public class HbciDeleteConsent implements DeleteConsent {

    private final HbciLogResolver logResolver = new HbciLogResolver(getClass());

    @Override
    public CompletableFuture<Result<DeleteConsentBody>> execute(ServiceContext<DeleteConsentRequest> ctx) {
        logResolver.log("Delete consent for {}", ctx);
        var consents = ctx.getRequestScoped().consentAccess().findByCurrentServiceSessionOrderByModifiedDesc();
        if (consents.isEmpty()) {
            return CompletableFuture.completedFuture(new ErrorResult<>("No consent available"));
        }

        for (var consent : consents) {
            logResolver.log("Removing remaining consent for session {}", ctx);
            ctx.getRequestScoped().consentAccess().delete(consent);
        }

        return CompletableFuture.completedFuture(new SuccessResult<>(new DeleteConsentBody()));
    }
}
