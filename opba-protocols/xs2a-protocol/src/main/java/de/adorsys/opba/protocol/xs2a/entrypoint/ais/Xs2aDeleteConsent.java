package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import de.adorsys.opba.protocol.api.ais.DeleteConsent;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.DeleteConsentRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DeleteConsentBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.AbortConsent;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service("xs2aDeleteConsent")
public class Xs2aDeleteConsent implements DeleteConsent {

    private final AbortConsent abortConsent;
    private final ConsentContextLoadingService contextLoader;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    @Transactional
    public CompletableFuture<Result<DeleteConsentBody>> execute(ServiceContext<DeleteConsentRequest> ctx) {
        logResolver.log("Delete consent for {}", ctx);
        var singleConsent = ctx.getRequestScoped().consentAccess().findSingleByCurrentServiceSession();
        if (singleConsent.isEmpty()) {
            return CompletableFuture.completedFuture(new ErrorResult<>("No consent available"));
        }

        logResolver.log("Removing single consent for session {}", ctx);
        // Fail if ASPSP does not abort consent because it might still be active
        abortConsent.abortConsent(contextLoader.contextFromConsent(singleConsent, ctx.getRequestScoped()));
        // Remove any remaining consents, ignore failures as they are probably inactive
        for (var consent : ctx.getRequestScoped().consentAccess().findByCurrentServiceSessionOrderByModifiedDesc()) {
            logResolver.log("Removing remaining consent for session {}", ctx);
            try {
                abortConsent.abortConsent(contextLoader.contextFromConsent(consent));
            } catch (Exception ex) {
                logResolver.log("Failed removing remaining consent for session {}", ctx, ex);
            }
        }

        return CompletableFuture.completedFuture(new SuccessResult<>(new DeleteConsentBody()));
    }
}
