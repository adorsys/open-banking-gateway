package de.adorsys.opba.protocol.sandbox.hbci.service;

import de.adorsys.multibanking.domain.PaymentStatus;
import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.MapRegexUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.repository.HbciSandboxPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HbciSandboxPaymentService {

    public static final String MAGIC_FLAG_TO_ACCEPT_PAYMENT_IMMEDIATELY = "!accept_immediately!";

    private final HbciSandboxPaymentRepository paymentRepository;

    @Transactional
    public void createPayment(HbciSandboxContext context, boolean instantPayment) {
        String paymentBodyKey = instantPayment ? "GV\\.InstantUebSEPA1\\.sepapain" : "GV\\.UebSEPA\\d+\\.sepapain";
        String paymentBody = MapRegexUtil.getDataRegex(context.getRequestData(), paymentBodyKey);
        HbciSandboxPayment payment = new HbciSandboxPayment();
        payment.setOrderReference(context.getOrderReference());
        payment.setDeduceFrom(findDebitorAccount(paymentBody));
        payment.setSendTo(findCreditorAccount(paymentBody));
        payment.setAmount(new BigDecimal(findAmount(paymentBody)));
        payment.setCurrency(findCurrency(paymentBody));
        payment.setRemittanceUnstructured(findRemittanceUnstructuredWithEmptyDefault(paymentBody));
        payment.setOwnerLogin(context.getUser().getLogin());
        payment.setInstantPayment(instantPayment);
        if (!payment.getDeduceFrom().endsWith(context.getAccountNumberRequestedBeforeSca())) {
            throw new IllegalStateException("Wrong account number referenced, not matches debitor account number");
        }
        paymentRepository.save(payment);
    }

    @Transactional
    public void createPaymentIfNeededAndPossibleFromContext(HbciSandboxContext context) {
        if (null != MapRegexUtil.getDataRegex(context.getRequestData(), "GV\\.UebSEPA\\d+\\.sepapain")) {
            createPayment(context, false);
            return;
        }

        if (null != MapRegexUtil.getDataRegex(context.getRequestData(), "GV\\.InstantUebSEPA1\\.sepapain")) {
            createPayment(context, true);
            return;
        }
    }

    @Transactional
    public void acceptPayment(HbciSandboxContext context) {
        String orderReference = MapRegexUtil.getDataRegex(context.getRequestData(), "GV\\.TAN2Step\\d+\\.orderref");
        HbciSandboxPayment payment = paymentRepository.findByOwnerLoginAndOrderReference(context.getUser().getLogin(), orderReference)
                .orElseThrow(() -> new IllegalStateException(String.format("Order with reference %s of user %s not found", orderReference, context.getUser().getLogin())));
        // Some magic flag to accept payment immediately
        if (null != payment.getRemittanceUnstructured() && payment.getRemittanceUnstructured().contains(MAGIC_FLAG_TO_ACCEPT_PAYMENT_IMMEDIATELY)) {
            payment.setStatus(PaymentStatus.ACSC); // save not needed as is managed entity
        } else {
            payment.setStatus(PaymentStatus.ACTC); // save not needed as is managed entity
        }
    }

    @Transactional
    public void paymentFromDatabaseToContext(HbciSandboxContext context, String paymentId) {
        HbciSandboxPayment payment = paymentRepository.findByOwnerLoginAndOrderReference(context.getUser().getLogin(), paymentId)
                .orElseThrow(() -> new IllegalStateException(String.format("Order (payment) with reference %s of user %s not found", paymentId, context.getUser().getLogin())));
        context.setPayment(payment);
    }

    @Transactional
    @Scheduled(fixedDelayString = "${hbci.payment-schedule}")
    public void acceptPayments() {
        paymentRepository.findByStatus(PaymentStatus.ACTC).forEach(it -> {
            it.setStatus(PaymentStatus.ACSC); // save not needed as is managed entity
        });
    }


    private String findCreditorAccount(String paymentBody) {
        Pattern pattern = Pattern.compile("<CdtrAcct><Id><IBAN>([0-9A-Z]+)</IBAN></Id></CdtrAcct>");
        Matcher matcher = pattern.matcher(paymentBody);
        if (!matcher.find()) {
            throw new IllegalStateException("No creditor account");
        }
        return matcher.group(1);
    }

    private String findDebitorAccount(String paymentBody) {
        Pattern pattern = Pattern.compile("<DbtrAcct><Id><IBAN>([0-9A-Z]+)</IBAN></Id></DbtrAcct>");
        Matcher matcher = pattern.matcher(paymentBody);
        if (!matcher.find()) {
            throw new IllegalStateException("No debitor account");
        }
        return matcher.group(1);
    }

    private String findAmount(String paymentBody) {
        Pattern pattern = Pattern.compile("<InstdAmt Ccy=\"[A-Z]+\">([0-9.]+)</InstdAmt>");
        Matcher matcher = pattern.matcher(paymentBody);
        if (!matcher.find()) {
            throw new IllegalStateException("No amount");
        }
        return matcher.group(1);
    }

    private String findCurrency(String paymentBody) {
        Pattern pattern = Pattern.compile("InstdAmt Ccy=\"([A-Z]+)\"");
        Matcher matcher = pattern.matcher(paymentBody);
        if (!matcher.find()) {
            throw new IllegalStateException("No currency");
        }
        return matcher.group(1);
    }

    private String findRemittanceUnstructuredWithEmptyDefault(String paymentBody) {
        Pattern pattern = Pattern.compile("<RmtInf><Ustrd>(.+)</Ustrd></RmtInf>");
        Matcher matcher = pattern.matcher(paymentBody);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1);
    }
}
