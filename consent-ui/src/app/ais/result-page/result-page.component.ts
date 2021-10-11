import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {ActivatedRoute, ActivatedRouteSnapshot} from '@angular/router';

import {StubUtil} from '../../common/utils/stub-util';
import {AisConsentToGrant} from '../common/dto/ais-consent';
import {SessionService} from '../../common/session.service';
import {ConsentUtil} from '../common/consent-util';
import {ApiHeaders} from '../../api/api.headers';
import {AuthStateConsentAuthorizationService, UpdateConsentAuthorizationService} from '../../api';

@Component({
  selector: 'consent-app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {
  public static ROUTE = 'consent-result';

  public finTechName: string;
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
    this.finTechName = this.sessionService.getFintechName(this.authorizationId);
    const redirectCode = this.route.queryParams.redirectCode;
    this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    this.loadRedirectUri(this.authorizationId, redirectCode);
  }

  private onConfirm() {
    window.location.href = this.redirectTo;
  }

  private onDeny() {
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

  private loadRedirectUri(authId: string, redirectCode: string) {
    this.authStateConsentAuthorizationService.authUsingGET(authId, redirectCode, 'response').subscribe((res) => {
      console.log(res);
      this.sessionService.setRedirectCode(authId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
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
