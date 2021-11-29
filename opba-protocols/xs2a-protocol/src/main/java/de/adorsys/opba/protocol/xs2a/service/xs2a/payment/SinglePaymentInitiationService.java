package de.adorsys.opba.protocol.xs2a.service.xs2a.payment;

import com.vdurmont.semver4j.Semver;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentInitationRequestResponse201;
import org.flowable.engine.delegate.DelegateExecution;

import java.util.Set;

public interface SinglePaymentInitiationService {

     Set<Semver.VersionDiff> VERSION_DIFFS = Set.of(Semver.VersionDiff.MAJOR, Semver.VersionDiff.MINOR);

     void doValidate(DelegateExecution execution, Xs2aPisContext context);

     Response<PaymentInitationRequestResponse201> doExecution(DelegateExecution execution, Xs2aPisContext context);

     boolean isXs2aApiVersionSupported(String apiVersion);

}
