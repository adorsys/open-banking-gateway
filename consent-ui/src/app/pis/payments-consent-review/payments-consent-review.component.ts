import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { SessionService } from '../../common/session.service';
import { PsuAuthRequest, UpdateConsentAuthorizationService } from '../../api';
import { SharedRoutes } from '../../ais/entry-page/initiation/common/shared-routes';
import { AccountAccessLevel } from '../../ais/common/dto/ais-consent';
import { StubUtil } from '../../common/utils/stub-util';
import { ApiHeaders } from '../../api/api.headers';
import { PaymentUtil } from '../common/payment-util';
import { PisPayment } from '../common/models/pis-payment.model';

@Component({
  selector: 'consent-app-payments-consent-review',
  templateUrl: './payments-consent-review.component.html',
  styleUrls: ['./payments-consent-review.component.scss']
})
export class PaymentsConsentReviewComponent implements OnInit {
  public static ROUTE = SharedRoutes.REVIEW;
  accountAccessLevel = AccountAccessLevel;
  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;
  public payment: PisPayment;
  private authorizationId: string;

  constructor(
    private location: Location,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.payment = PaymentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }

  onConfirm() {
    const body = { extras: this.payment.extras } as PsuAuthRequest;

    // if (this.payment) {
    //   body.consentAuth = { consent: this.payment.consent } as ConsentAuth;
    // }

    console.log(body);

    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationId,
        StubUtil.X_XSRF_TOKEN,
        StubUtil.X_REQUEST_ID,
        this.sessionService.getRedirectCode(this.authorizationId),
        body,
        'response'
      )
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  onBack() {
    this.location.back();
  }
}
