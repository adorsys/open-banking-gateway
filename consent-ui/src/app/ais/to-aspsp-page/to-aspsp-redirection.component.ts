import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { AisConsentToGrant } from '../common/dto/ais-consent';
import { StubUtil } from '../common/stub-util';
import { SessionService } from '../../common/session.service';
import { ConsentUtil } from '../common/consent-util';
import { ApiHeaders } from '../../api/api.headers';
import { ConsentAuthorizationService, DenyRequest } from '../../api';

@Component({
  selector: 'consent-app-to-aspsp-redirection',
  templateUrl: './to-aspsp-redirection.component.html',
  styleUrls: ['./to-aspsp-redirection.component.scss']
})
export class ToAspspRedirectionComponent implements OnInit {
  public static ROUTE = 'to-aspsp-redirection';

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  redirectTo: string;

  private authorizationId: string;
  private aisConsent: AisConsentToGrant;

  constructor(
    private location: Location,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private consentAuthorisation: ConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.activatedRoute.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.loadRedirectUri();
    });
  }

  private loadRedirectUri() {
    this.consentAuthorisation
      .authUsingGET(this.authorizationId, this.sessionService.getRedirectCode(this.authorizationId), 'response')
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  onDeny() {
    this.consentAuthorisation
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
