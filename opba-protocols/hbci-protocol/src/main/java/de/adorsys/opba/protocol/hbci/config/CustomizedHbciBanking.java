package de.adorsys.opba.protocol.hbci.config;

import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.AbstractResponse;
import de.adorsys.multibanking.domain.response.PaymentResponse;
import de.adorsys.multibanking.domain.response.TransactionAuthorisationResponse;
import de.adorsys.multibanking.domain.transaction.AbstractPayment;
import de.adorsys.multibanking.domain.transaction.TransactionAuthorisation;
import de.adorsys.multibanking.hbci.HbciBanking;
import de.adorsys.multibanking.hbci.HbciBpdUpdCallback;
import de.adorsys.multibanking.hbci.HbciCacheHandler;
import de.adorsys.multibanking.hbci.HbciExceptionHandler;
import de.adorsys.multibanking.hbci.job.ScaAwareJob;
import de.adorsys.multibanking.hbci.job.SinglePaymentJob;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.opba.protocol.hbci.service.protocol.CustomizedOnlineBankingService;
import lombok.SneakyThrows;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIProduct;

import java.lang.reflect.Method;

import static de.adorsys.multibanking.domain.ScaStatus.FINALISED;

public class CustomizedHbciBanking extends HbciBanking implements CustomizedOnlineBankingService {
    private long sysIdExpirationTimeMs;
    private long updExpirationTimeMs;

    public CustomizedHbciBanking(HBCIProduct hbciProduct, long sysIdExpirationTimeMs, long updExpirationTimeMs) {
        super(hbciProduct, sysIdExpirationTimeMs, updExpirationTimeMs);
        this.sysIdExpirationTimeMs = sysIdExpirationTimeMs;
        this.updExpirationTimeMs = updExpirationTimeMs;
    }

    @SneakyThrows
    public PaymentResponse executePayment(TransactionRequest<? extends AbstractPayment> request, String endToEndId) {
        HbciConsent hbciConsent = (HbciConsent) request.getBankApiConsentData();
        hbciConsent.checkUpdSysIdCache(sysIdExpirationTimeMs, updExpirationTimeMs);

        try {
            if (hbciConsent.getHbciTanSubmit() == null || hbciConsent.getStatus() == FINALISED) {

                Method createCallback = HbciCacheHandler.class.getMethod("createCallback", TransactionRequest.class);
                createCallback.setAccessible(true);
                HbciBpdUpdCallback hbciCallback = (HbciBpdUpdCallback) createCallback.invoke(null, request);

                Method createScaJob = HbciBanking.class.getDeclaredMethod("createScaJob", TransactionRequest.class);
                createScaJob.setAccessible(true);
                ScaAwareJob<? extends AbstractPayment, PaymentResponse> paymentJob = (ScaAwareJob<? extends AbstractPayment, PaymentResponse>) createScaJob.invoke(this, request);

                if (paymentJob instanceof SinglePaymentJob) {
                    paymentJob = new CustomizedSinglePaymentJob((TransactionRequest) request, (SinglePaymentJob) paymentJob, endToEndId);
                }

                PaymentResponse response = paymentJob.execute(hbciCallback);
                response.setBankApiConsentData(hbciCallback.updateConsentUpd(hbciConsent));

                return response;
            } else {
                Method transactionAuthorisation = HbciBanking.class.getDeclaredMethod("transactionAuthorisation", TransactionRequest.class);
                transactionAuthorisation.setAccessible(true);
                TransactionAuthorisationResponse<? extends AbstractResponse> transactionAuthorisationResponse =
                        (TransactionAuthorisationResponse<? extends AbstractResponse>) transactionAuthorisation.invoke(new TransactionAuthorisation<>(this, request));

                hbciConsent.afterTransactionAuthorisation(transactionAuthorisationResponse.getScaStatus());

                return (PaymentResponse) transactionAuthorisationResponse.getJobResponse();
            }
        } catch (HBCI_Exception e) {
            Method handleHbciException = HbciExceptionHandler.class.getMethod("handleHbciException", HBCI_Exception.class);
            handleHbciException.setAccessible(true);
            throw (HBCI_Exception) handleHbciException.invoke(null, e);
        }
    }
}
