package de.adorsys.opba.consent.embedded.rest.api.domain.payment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import de.adorsys.opba.consent.embedded.rest.api.domain.common.AccountReferenceTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.common.AddressTO;
import de.adorsys.opba.consent.embedded.rest.api.domain.common.AmountTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SinglePaymentTO {
    private String paymentId;
    private String endToEndIdentification;
    private AccountReferenceTO debtorAccount;
    private AmountTO instructedAmount;
    private AccountReferenceTO creditorAccount;
    private String creditorAgent;
    private String creditorName;
    private AddressTO creditorAddress;
    private String remittanceInformationUnstructured;
    private TransactionStatusTO paymentStatus;
    private PaymentProductTO paymentProduct;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate requestedExecutionDate;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime requestedExecutionTime;
}
