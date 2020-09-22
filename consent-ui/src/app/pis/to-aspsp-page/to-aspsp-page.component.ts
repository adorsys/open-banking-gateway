import { Component, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { Action } from '../../common/utils/action';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { AuthStateConsentAuthorizationService, DenyRequest, UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { PaymentUtil } from '../common/payment-util';
import { PisPayment } from '../common/models/pis-payment.model';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'consent-app-to-aspsp-page',
  templateUrl: './to-aspsp-page.component.html',
  styleUrls: ['./to-aspsp-page.component.scss']
})
export class ToAspspPageComponent implements OnInit {
  public static ROUTE = 'to-aspsp-redirection';

  public finTechName: string;
  public aspspName: string;
  public payment = Action.PAYMENT;
  public authorizationId: string;

  redirectTo: string;
  private pisPayment: PisPayment;

  constructor(
    private location: Location,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    combineLatest([this.activatedRoute.parent.params, this.activatedRoute.parent.queryParams]).subscribe((res) => {
      const pathParams = res[0];
      const query = res[1];

      this.authorizationId = pathParams.authId;
      if (query.redirectCode) {
        this.sessionService.setRedirectCode(this.authorizationId, query.redirectCode);
      }

      this.aspspName = this.sessionService.getBankName(pathParams.authId);
      this.finTechName = this.sessionService.getFintechName(pathParams.authId);
      this.pisPayment = PaymentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.loadRedirectUri();
    });
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
      .subscribe((res) => {
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  private loadRedirectUri() {
    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationId, this.sessionService.getRedirectCode(this.authorizationId), 'response')
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
      });
  }
}
