package de.adorsys.opba.consent.embedded.rest.api.domain;

import de.adorsys.opba.consent.embedded.rest.api.domain.payment.BulkPaymentTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.payment.PaymentTypeTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.payment.PeriodicPaymentTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.payment.SinglePaymentTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.payment.TransactionStatusTO;

public class PaymentAuthorizeResponse extends AuthorizeResponse  {
	private final SinglePaymentTO singlePayment;
	private final BulkPaymentTO bulkPayment;
	private final PeriodicPaymentTO periodicPayment;
	
	private String authMessageTemplate;
	
	public PaymentAuthorizeResponse() {
		this.singlePayment = null;
		this.bulkPayment = null;
		this.periodicPayment = null;
	}

	public PaymentAuthorizeResponse(SinglePaymentTO singlePayment) {
		this.singlePayment = singlePayment;
		this.bulkPayment = null;
		this.periodicPayment = null;
	}
	public PaymentAuthorizeResponse(BulkPaymentTO bulkPayment) {
		this.singlePayment = null;
		this.bulkPayment = bulkPayment;
		this.periodicPayment = null;
	}
	public PaymentAuthorizeResponse(PeriodicPaymentTO periodicPayment) {
		this.singlePayment = null;
		this.bulkPayment = null;
		this.periodicPayment = periodicPayment;
	}
	public PaymentAuthorizeResponse(PaymentTypeTO paymentType, Object payment) {
		switch (paymentType) {
			case SINGLE:
				this.singlePayment = (SinglePaymentTO)payment;
				this.bulkPayment = null;
				this.periodicPayment = null;
				break;
			case BULK:
				this.singlePayment = null;
				this.bulkPayment = (BulkPaymentTO)payment;
				this.periodicPayment = null;
				break;
			default:
				this.singlePayment = null;
				this.bulkPayment = null;
				this.periodicPayment = (PeriodicPaymentTO)payment;
		}	
	}
	public SinglePaymentTO getSinglePayment() {
		return singlePayment;
	}
	public BulkPaymentTO getBulkPayment() {
		return bulkPayment;
	}
	public PeriodicPaymentTO getPeriodicPayment() {
		return periodicPayment;
	}
	public String getAuthMessageTemplate() {
		return authMessageTemplate;
	}
	public void setAuthMessageTemplate(String authMessageTemplate) {
		this.authMessageTemplate = authMessageTemplate;
	}
	public void updatePaymentStatus(TransactionStatusTO paymentStatus) {
		if(singlePayment!=null) {
			singlePayment.setPaymentStatus(paymentStatus);
		} else if (bulkPayment!=null) {
			bulkPayment.setPaymentStatus(paymentStatus);
		} else if (periodicPayment!=null) {
			periodicPayment.setPaymentStatus(paymentStatus);
		}
	}
	
}
