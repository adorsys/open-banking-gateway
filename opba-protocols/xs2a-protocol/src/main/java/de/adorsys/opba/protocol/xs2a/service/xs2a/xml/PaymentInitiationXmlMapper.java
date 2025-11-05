package de.adorsys.opba.protocol.xs2a.service.xs2a.xml;

import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentInitiationXmlMapper {

    PaymentInitiationXmlMapper INSTANCE = Mappers.getMapper(PaymentInitiationXmlMapper.class);

    @Mapping(target = "customerCreditTransferInitiation.groupHeader.messageId", source = "endToEndIdentification")
    @Mapping(target = "customerCreditTransferInitiation.groupHeader.creationDateTime", expression = "java(java.time.OffsetDateTime.now().format(java.time.format.DateTimeFormatter.ISO_INSTANT))")
    @Mapping(target = "customerCreditTransferInitiation.groupHeader.numberOfTransactions", constant = "1") // Assuming single payment
    @Mapping(target = "customerCreditTransferInitiation.groupHeader.controlSum", source = "instructedAmount.amount")
    @Mapping(target = "customerCreditTransferInitiation.groupHeader.initiatingParty.partyIdentification.organisationIdentification.otherIdentification.id", source = "debtorAccount.iban")
    @Mapping(target = "customerCreditTransferInitiation.groupHeader.initiatingParty.partyIdentification.organisationIdentification.otherIdentification.schemeName.proprietary", constant = "PISP")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.paymentInformationId", source = "endToEndIdentification")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.paymentMethod", constant = "TRF")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.debtor.name", constant = "PISP User")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.debtorAgent.financialInstitutionIdentification.bic", constant = "DEUTDEFFXXX")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.numberOfTransactions", constant = "1")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.controlSum", source = "instructedAmount.amount")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.paymentTypeInformation.serviceLevel.code", constant = "SEPA")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.requestedExecutionDate",
            expression = "java(java.time.LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE))")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.debtorAccount.accountIdentification.iban", source = "debtorAccount.iban")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.chargeBearer", constant = "SLEV")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.creditTransferTransactionInformation.paymentIdentification.endToEndId", source = "endToEndIdentification")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.creditTransferTransactionInformation.amount.instructedAmount.currency", source = "instructedAmount.currency")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.creditTransferTransactionInformation.amount.instructedAmount.value", source = "instructedAmount.amount")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.creditTransferTransactionInformation.creditor.name", source = "creditorName")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.creditTransferTransactionInformation.creditorAccount.accountIdentification.iban", source = "creditorAccount.iban")
    @Mapping(target = "customerCreditTransferInitiation.paymentInformation.creditTransferTransactionInformation.remittanceInformation.unstructured", source = "remittanceInformationUnstructured")
    Pain00100103Document map(PaymentInitiationJson paymentInitiationJson);
}
