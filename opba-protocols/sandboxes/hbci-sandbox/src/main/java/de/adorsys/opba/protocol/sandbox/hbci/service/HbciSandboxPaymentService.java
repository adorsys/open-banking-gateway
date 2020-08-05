package de.adorsys.opba.protocol.sandbox.hbci.service;

import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.repository.HbciSandboxPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HbciSandboxPaymentService {

    private final HbciSandboxPaymentRepository paymentRepository;

    @Transactional
    public void createPayment(HbciSandboxContext context) {
        HbciSandboxPayment payment = new HbciSandboxPayment();
        //payment.setFrom(from);
        //payment.setTo(to);
        //payment.setAmount(amount);
        //payment.setOwnerLogin(context.getRequestUserLogin());
        //payment.setStatus(PaymentStatus.ACSP);
        //return paymentRepository.save(payment);
    }
}
