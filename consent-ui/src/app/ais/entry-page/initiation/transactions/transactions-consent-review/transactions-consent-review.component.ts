import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { SharedRoutes } from '../../common/shared-routes';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { SessionService } from '../../../../../common/session.service';
import { AccountAccessLevel, AisConsentToGrant } from '../../../../common/dto/ais-consent';
import { StubUtil } from '../../../../../common/utils/stub-util';
import { ConsentUtil } from '../../../../common/consent-util';
import { ApiHeaders } from '../../../../../api/api.headers';
import { ConsentAuth, ConsentAuthorizationService, PsuAuthRequest } from '../../../../../api';

@Component({
  selector: 'consent-app-transactions-consent-review',
  templateUrl: './transactions-consent-review.component.html',
  styleUrls: ['./transactions-consent-review.component.scss']
})
export class TransactionsConsentReviewComponent implements OnInit {
  constructor(
    private location: Location,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private consentAuthorisation: ConsentAuthorizationService
  ) {}

  public static ROUTE = SharedRoutes.REVIEW;
  accountAccessLevel = AccountAccessLevel;

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  public aisConsent: AisConsentToGrant;

  private authorizationId: string;

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }

  onConfirm() {
    const body = { extras: this.aisConsent.extras } as PsuAuthRequest;

    if (this.aisConsent) {
      body.consentAuth = { consent: this.aisConsent.consent } as ConsentAuth;
    }

    this.consentAuthorisation
      .embeddedUsingPOST(
        this.authorizationId,
        StubUtil.X_XSRF_TOKEN,
        StubUtil.X_REQUEST_ID,
        this.sessionService.getRedirectCode(this.authorizationId),
        body,
        'response'
      )
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  onBack() {
    this.location.back();
  }
}
