package de.adorsys.opba.protocol.hbci.config;

import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.transaction.SinglePayment;
import de.adorsys.multibanking.hbci.job.SinglePaymentJob;
import lombok.experimental.Delegate;
import org.kapott.hbci.GV.AbstractHBCIJob;
import org.kapott.hbci.passport.PinTanPassport;

@SuppressWarnings({"PMD.UnusedPrivateField"})
public class CustomizedSinglePaymentJob extends SinglePaymentJob {

    @Delegate(excludes = WithoutJobMessage.class)
    private final SinglePaymentJob singlePaymentJob;
    private final String endToEndId;

    public CustomizedSinglePaymentJob(TransactionRequest<SinglePayment> transactionRequest, SinglePaymentJob singlePaymentJob, String endToEndId) {
        super(transactionRequest);
        this.singlePaymentJob = singlePaymentJob;
        this.endToEndId = endToEndId;
    }

    @Override
    public AbstractHBCIJob createJobMessage(PinTanPassport passport) {
        AbstractHBCIJob jobMessage = super.createJobMessage(passport);

        jobMessage.setParam("endtoendid", endToEndId);

        return jobMessage;
    }

    private interface WithoutJobMessage {
        AbstractHBCIJob createJobMessage(PinTanPassport passport);
    }
}
