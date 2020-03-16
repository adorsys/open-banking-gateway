import {Component, OnInit} from '@angular/core';
import {SharedRoutes} from "../../common/shared-routes";
import {AccountAccessLevel, AisConsentToGrant} from "../../../../common/dto/ais-consent";
import {StubUtil} from "../../../../common/stub-util";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder} from "@angular/forms";
import {SessionService} from "../../../../../common/session.service";
import {ConsentUtil} from "../../../../common/consent-util";
import {ApiHeaders} from "../../../../../api/api.headers";
import {ConsentAuth, ConsentAuthorizationService, PsuAuthRequest} from "../../../../../api";

@Component({
  selector: 'consent-app-accounts-consent-review',
  templateUrl: './accounts-consent-review.component.html',
  styleUrls: ['./accounts-consent-review.component.scss']
})
export class AccountsConsentReviewComponent implements OnInit {

  accountAccessLevel = AccountAccessLevel;

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  public static ROUTE = SharedRoutes.REVIEW;
  public aisConsent: AisConsentToGrant;

  private authorizationId: string;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private consentAuthorisation: ConsentAuthorizationService
  ) {
  }

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }

  onConfirm() {
    const body = {extras: this.aisConsent.extras} as PsuAuthRequest;

    if (this.aisConsent) {
      body.consentAuth = {consent: this.aisConsent.consent} as ConsentAuth;
    }

    this.consentAuthorisation.embeddedUsingPOST(
      this.authorizationId,
      StubUtil.X_XSRF_TOKEN,
      StubUtil.X_REQUEST_ID,
      this.sessionService.getRedirectCode(this.authorizationId),
      body,
      'response'
    ).subscribe(res => {
      this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      window.location.href = res.headers.get(ApiHeaders.LOCATION);
    })
  }
}
