import { Component, Input, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AuthConsentState } from '../../ais/common/dto/auth-state';
import { AccountAccessLevel, AisConsentToGrant } from '../../ais/common/dto/ais-consent';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { DenyRequest, UpdateConsentAuthorizationService } from '../../api';
import { ConsentUtil } from '../../ais/common/consent-util';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-payment-access-selection',
  templateUrl: './consent-payment-access-selection.component.html',
  styleUrls: ['./consent-payment-access-selection.component.scss']
})
export class ConsentPaymentAccessSelectionComponent implements OnInit {
  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  @Input() paymentReviewPage: string;

  public paymentAccessForm: FormGroup;
  public state: AuthConsentState;
  public consent: AisConsentToGrant;

  private authorizationId: string;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {
    this.paymentAccessForm = this.formBuilder.group({});
  }

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.state = this.sessionService.getPaymentState(this.authorizationId, () => new AuthConsentState());
      if (!this.hasGeneralViolations()) {
        this.moveToReviewPayment();
      }

      this.consent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }

  hasGeneralViolations(): boolean {
    return this.state.hasGeneralViolation();
  }

  onConfirm() {
    this.updatePaymentObject();
    this.moveToReviewPayment();
  }

  onDeny() {
    this.updateConsentAuthorizationService
      .denyUsingPOST(
        this.authorizationId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        {} as DenyRequest,
        'response'
      )
      .subscribe(res => {
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  private updatePaymentObject() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    if (this.state.hasGeneralViolation()) {
      consentObj.extras = consentObj.extras ? consentObj.extras : {};
      this.state
        .getGeneralViolations()
        .forEach(it => (consentObj.extras[it.code] = this.paymentAccessForm.get(it.code).value));
    }

    this.sessionService.setConsentObject(this.authorizationId, consentObj);
  }

  private moveToReviewPayment() {
    this.router.navigate([this.paymentReviewPage], { relativeTo: this.activatedRoute });
  }
}
