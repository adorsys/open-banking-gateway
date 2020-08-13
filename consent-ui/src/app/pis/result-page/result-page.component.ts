import { Component, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { AisConsentToGrant } from '../../ais/common/dto/ais-consent';
import { Location } from '@angular/common';
import { SessionService } from '../../common/session.service';
import { AuthStateConsentAuthorizationService, DenyRequest, UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { PaymentUtil } from '../common/payment-util';
import { PisPayment } from '../common/models/pis-payment.model';

@Component({
  selector: 'consent-app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {
  public static ROUTE = 'consent-result';

  public finTechName: string;
  public title = 'Payment was successful';
  public subtitle = 'Paid 100EUR to IBAN12345';
  redirectTo: string;

  private route: ActivatedRouteSnapshot;
  private payment: PisPayment;

  private authorizationId: string;

  constructor(
    private location: Location,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.route = this.activatedRoute.snapshot;
    this.authorizationId = this.route.parent.params.authId;
    this.finTechName = this.sessionService.getFintechName(this.authorizationId);
    const redirectCode = this.route.queryParams.redirectCode;
    this.payment = PaymentUtil.getOrDefault(this.authorizationId, this.sessionService);
    this.loadRedirectUri(this.authorizationId, redirectCode);
  }

  onConfirm() {
    window.location.href = this.redirectTo;
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

  private loadRedirectUri(authId: string, redirectCode: string) {
    this.authStateConsentAuthorizationService.authUsingGET(authId, redirectCode, 'response').subscribe(res => {
      console.log(res);
      this.sessionService.setRedirectCode(authId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
    });
  }

  public confirm(value: boolean): void {
    if (value) {
      this.onConfirm();
    } else {
      this.onDeny();
    }
  }
}
