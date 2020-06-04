import { Component, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { AisConsentToGrant } from '../common/dto/ais-consent';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { ConsentUtil } from '../common/consent-util';
import { ApiHeaders } from '../../api/api.headers';
import { UpdateConsentAuthorizationService } from '../../api';
import { AuthStateConsentAuthorizationService, DenyRequest } from '../../api';
import { Location } from '@angular/common';

@Component({
  selector: 'consent-app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {
  public static ROUTE = 'consent-result';

  public finTechName = StubUtil.FINTECH_NAME;
  public title = 'Consent has been granted';
  redirectTo: string;

  private route: ActivatedRouteSnapshot;
  private aisConsent: AisConsentToGrant;

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
    const redirectCode = this.route.queryParams.redirectCode;
    this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
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
    this.authStateConsentAuthorizationService
      .authUsingGET(authId, redirectCode, 'response')
      .subscribe(res => {
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
