package de.adorsys.opba.consent.rest.api.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@SuppressWarnings("LineLength")
@Data
@ApiModel(description = "General purpose transport object for consent information.")
public class ConsentAuthTO {
  /*
   * This is the CSRF-State String of the ConsentAuthorisationApi. It is a
   * transient reference of the consent request. It encodes a key that is used to
   * encrypt information stored in the corresponding ConsentAuthSessionCookie.
   */
  @ApiModelProperty("This is the CSRF-State String of the ConsentAuthorisationApi. It is a transient reference of the consent request. It encodes a key that is used to encrypt information stored in the corresponding ConsentAuthSessionCookie.")
  private String consentAuthState;

  /*
   * List of sca methods for selection if necessary.
   */
  @ApiModelProperty("List of sca methods for selection if necessary.")
  private List<ScaUserDataTO> scaMethods;
  @ApiModelProperty(value = "An identification provided by the ASPSP for the later identification of the "
      + "authentication method selection.", required = true, example = "myAuthenticationID")
  private String authenticationMethodId;

  /*
   * The sca status. Used to manage the consent authorization flows.
   */
  @ApiModelProperty("The sca status. Used to manage the consent authorization flows.")
  private ScaStatusTO scaStatus;

  private List<AccountDetailsTO> accounts;
  private AisConsentTO consent;
  private String authMessageTemplate;

  private final SinglePaymentTO singlePayment;
  private final BulkPaymentTO bulkPayment;
  private final PeriodicPaymentTO periodicPayment;

  private String psuId;
  private String psuCorporateId;

  public ConsentAuthTO() {
    this.singlePayment = null;
    this.bulkPayment = null;
    this.periodicPayment = null;
  }

  public ConsentAuthTO(SinglePaymentTO singlePayment) {
    this.singlePayment = singlePayment;
    this.bulkPayment = null;
    this.periodicPayment = null;
  }

  public ConsentAuthTO(BulkPaymentTO bulkPayment) {
    this.singlePayment = null;
    this.bulkPayment = bulkPayment;
    this.periodicPayment = null;
  }

  public ConsentAuthTO(PeriodicPaymentTO periodicPayment) {
    this.singlePayment = null;
    this.bulkPayment = null;
    this.periodicPayment = periodicPayment;
  }

  public ConsentAuthTO(PaymentTypeTO paymentType, Object payment) {
    switch (paymentType) {
    case SINGLE:
      this.singlePayment = (SinglePaymentTO) payment;
      this.bulkPayment = null;
      this.periodicPayment = null;
      break;
    case BULK:
      this.singlePayment = null;
      this.bulkPayment = (BulkPaymentTO) payment;
      this.periodicPayment = null;
      break;
    default:
      this.singlePayment = null;
      this.bulkPayment = null;
      this.periodicPayment = (PeriodicPaymentTO) payment;
    }
  }

  public void updatePaymentStatus(TransactionStatusTO paymentStatus) {
    if (singlePayment != null) {
      singlePayment.setPaymentStatus(paymentStatus);
    } else if (bulkPayment != null) {
      bulkPayment.setPaymentStatus(paymentStatus);
    } else if (periodicPayment != null) {
      periodicPayment.setPaymentStatus(paymentStatus);
    }
  }

}
