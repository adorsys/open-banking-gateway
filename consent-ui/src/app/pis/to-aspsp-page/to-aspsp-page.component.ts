import { Component, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { Action } from '../../common/utils/action';
import { AisConsentToGrant } from '../../ais/common/dto/ais-consent';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { AuthStateConsentAuthorizationService, DenyRequest, UpdateConsentAuthorizationService } from '../../api';
import { ConsentUtil } from '../../ais/common/consent-util';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-to-aspsp-page',
  templateUrl: './to-aspsp-page.component.html',
  styleUrls: ['./to-aspsp-page.component.scss']
})
export class ToAspspPageComponent implements OnInit {
  public static ROUTE = 'to-aspsp-redirection';

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;
  public payment = Action.PAYMENT;

  redirectTo: string;

  private authorizationId: string;
  private aisConsent: AisConsentToGrant;

  constructor(
    private location: Location,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.activatedRoute.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.loadRedirectUri();
    });
  }

  private loadRedirectUri() {
    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationId, this.sessionService.getRedirectCode(this.authorizationId), 'response')
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
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
      .subscribe(res => {
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }
}
