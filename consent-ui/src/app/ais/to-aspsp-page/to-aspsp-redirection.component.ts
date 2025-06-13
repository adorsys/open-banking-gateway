import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { AisConsentToGrant } from '../common/dto/ais-consent';
import { StubUtil } from '../../common/utils/stub-util';
import { SessionService } from '../../common/session.service';
import { ConsentUtil } from '../common/consent-util';
import { ApiHeaders } from '../../api/api.headers';
import { Action } from '../../common/utils/action';
import { AuthStateConsentAuthorizationService, UpdateConsentAuthorizationService } from '../../api';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'consent-app-to-aspsp-redirection',
  templateUrl: './to-aspsp-redirection.component.html',
  styleUrls: ['./to-aspsp-redirection.component.scss'],
  standalone: false
})
export class ToAspspRedirectionComponent implements OnInit {
  public static ROUTE = 'to-aspsp-redirection';

  public finTechName: string;
  public aspspName: string;
  public account = Action.ACCOUNT;
  public authorizationId: string;

  redirectTo: string;
  private aisConsent: AisConsentToGrant;

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
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.loadRedirectUri();
    });
  }

  private loadRedirectUri() {
    localStorage.setItem(this.authorizationId, 'false');
    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationId, this.sessionService.getRedirectCode(this.authorizationId), 'response')
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  onDeny() {
    this.updateConsentAuthorizationService
      .denyUsingPOST(
        this.authorizationId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        'response'
      )
      .subscribe((res) => {
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }
}
