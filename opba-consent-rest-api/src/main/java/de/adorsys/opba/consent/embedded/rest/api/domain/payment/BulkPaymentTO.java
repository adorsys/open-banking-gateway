package de.adorsys.opba.consent.embedded.rest.api.domain.payment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import de.adorsys.opba.consent.embedded.rest.api.domain.common.AccountReferenceTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkPaymentTO {
    private String paymentId;
    private Boolean batchBookingPreferred;
    private AccountReferenceTO debtorAccount;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate requestedExecutionDate;
    private TransactionStatusTO paymentStatus;
    private List<SinglePaymentTO> payments;
    private PaymentProductTO paymentProduct;
}
