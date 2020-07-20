import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { AisConsentToGrant } from '../common/dto/ais-consent';
import { StubUtil } from '../../common/utils/stub-util';
import { SessionService } from '../../common/session.service';
import { ConsentUtil } from '../common/consent-util';
import { ApiHeaders } from '../../api/api.headers';
import { Action } from '../../common/utils/action';
import { AuthStateConsentAuthorizationService } from '../../api';
import { UpdateConsentAuthorizationService, DenyRequest } from '../../api';

@Component({
  selector: 'consent-app-to-aspsp-redirection',
  templateUrl: './to-aspsp-redirection.component.html',
  styleUrls: ['./to-aspsp-redirection.component.scss']
})
export class ToAspspRedirectionComponent implements OnInit {
  public static ROUTE = 'to-aspsp-redirection';

  public finTechName: string;
  public aspspName: string;
  public account = Action.ACCOUNT;

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
      this.aspspName = this.sessionService.getBankName(res.authId);
      this.finTechName = this.sessionService.getFintechName(res.authId);
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
