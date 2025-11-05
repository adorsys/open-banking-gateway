package de.adorsys.opba.protocol.xs2a.service.xs2a.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Document")
public class Pain00100103Document {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    private String xmlns = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03";

    @JsonProperty("CstmrCdtTrfInitn")
    private CustomerCreditTransferInitiation customerCreditTransferInitiation;

    @Data
    public static class CustomerCreditTransferInitiation {
        @JsonProperty("GrpHdr")
        private GroupHeader groupHeader;
        @JsonProperty("PmtInf")
        private PaymentInformation paymentInformation;
    }

    @Data
    public static class GroupHeader {
        @JsonProperty("MsgId")
        private String messageId;
        @JsonProperty("CreDtTm")
        private String creationDateTime;
        @JsonProperty("NbOfTxs")
        private String numberOfTransactions;
        @JsonProperty("CtrlSum")
        private String controlSum;
        @JsonProperty("InitgPty")
        private InitiatingParty initiatingParty;
    }

    @Data
    public static class InitiatingParty {
        @JsonProperty("Id")
        private PartyIdentification partyIdentification;
    }

    @Data
    public static class PartyIdentification {
        @JsonProperty("OrgId")
        private OrganisationIdentification organisationIdentification;
    }

    @Data
    public static class OrganisationIdentification {
        @JsonProperty("Othr")
        private OtherIdentification otherIdentification;
    }

    @Data
    public static class OtherIdentification {
        @JsonProperty("Id")
        private String id;
        @JsonProperty("SchmeNm")
        private SchemeName schemeName;
    }

    @Data
    public static class SchemeName {
        @JsonProperty("Prtry")
        private String proprietary;
    }

    @Data
    public static class PaymentInformation {
        @JsonProperty("PmtInfId")
        private String paymentInformationId;
        @JsonProperty("PmtMtd")
        private String paymentMethod;
        @JsonProperty("NbOfTxs")
        private String numberOfTransactions;
        @JsonProperty("CtrlSum")
        private String controlSum;
        @JsonProperty("PmtTpInf")
        private PaymentTypeInformation paymentTypeInformation;
        @JsonProperty("ReqdExctnDt")
        private String requestedExecutionDate;
        @JsonProperty("Dbtr")
        private Debtor debtor;
        @JsonProperty("DbtrAcct")
        private DebtorAccount debtorAccount;
        @JsonProperty("DbtrAgt")
        private DebtorAgent debtorAgent;
        @JsonProperty("ChrgBr")
        private String chargeBearer;
        @JsonProperty("CdtTrfTxInf")
        private CreditTransferTransactionInformation creditTransferTransactionInformation;
    }

    @Data
    public static class PaymentTypeInformation {
        @JsonProperty("SvcLvl")
        private ServiceLevel serviceLevel;
    }

    @Data
    public static class ServiceLevel {
        @JsonProperty("Cd")
        private String code;
    }

    @Data
    public static class Debtor {
        @JsonProperty("Nm")
        private String name = "PISP USER";

    }

    @Data
    public static class DebtorAccount {
        @JsonProperty("Id")
        private AccountIdentification accountIdentification;
    }

    @Data
    public static class AccountIdentification {
        @JsonProperty("IBAN")
        private String iban;
    }

    @Data
    public static class DebtorAgent {
        @JsonProperty("FinInstnId")
        private FinancialInstitutionIdentification financialInstitutionIdentification;
    }

    @Data
    public static class FinancialInstitutionIdentification {
        @JsonProperty("BIC")
        private String bic;
    }

    @Data
    public static class CreditTransferTransactionInformation {
        @JsonProperty("PmtId")
        private PaymentIdentification paymentIdentification;
        @JsonProperty("Amt")
        private AmountType amount;
        @JsonProperty("Cdtr")
        private Creditor creditor;
        @JsonProperty("CdtrAcct")
        private CreditorAccount creditorAccount;
        @JsonProperty("RmtInf")
        private RemittanceInformation remittanceInformation;
    }

    @Data
    public static class PaymentIdentification {
        @JsonProperty("EndToEndId")
        private String endToEndId;
    }

    @Data
    public static class AmountType {
        @JsonProperty("InstdAmt")
        private InstructedAmount instructedAmount;
    }

    @Data
    public static class InstructedAmount {
        @JacksonXmlProperty(isAttribute = true, localName = "Ccy")
        private String currency;
        @JacksonXmlText
        private String value;
    }

    @Data
    public static class Creditor {
        @JsonProperty("Nm")
        private String name;
    }

    @Data
    public static class CreditorAccount {
        @JsonProperty("Id")
        private AccountIdentification accountIdentification;
    }

    @Data
    public static class RemittanceInformation {
        @JsonProperty("Ustrd")
        private String unstructured;
    }
}
