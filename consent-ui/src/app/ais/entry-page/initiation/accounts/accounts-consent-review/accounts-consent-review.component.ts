import {Component, OnInit} from '@angular/core';
import {SharedRoutes} from "../../common/shared-routes";
import {AccountAccessLevel, AisConsentToGrant} from "../../../../common/dto/ais-consent";
import {StubUtil} from "../../../../common/stub-util";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder} from "@angular/forms";
import {SessionService} from "../../../../../common/session.service";
import {ConsentAuthorizationService} from "../../../../../api/consentAuthorization.service";
import {ConsentUtil} from "../../../../common/consent-util";

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

  private authorizationId: string;
  private aisConsent: AisConsentToGrant;

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
    const body = {
      extras: this.aisConsent.extras
    };

    if (this.aisConsent) {
      body['consentAuth'] = {consent: this.aisConsent.consent};
    }

    this.consentAuthorisation.embeddedUsingPOST(
      this.authorizationId,
      StubUtil.X_XSRF_TOKEN,
      StubUtil.X_REQUEST_ID,
      this.sessionService.getRedirectCode(this.authorizationId),
      body
    ).subscribe(res => {
    })
  }
}
