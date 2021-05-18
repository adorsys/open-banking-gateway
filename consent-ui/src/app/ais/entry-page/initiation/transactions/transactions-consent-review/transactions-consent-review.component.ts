import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {SharedRoutes} from '../../common/shared-routes';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SessionService} from '../../../../../common/session.service';
import {AccountAccessLevel, AisConsentToGrant} from '../../../../common/dto/ais-consent';
import {StubUtil} from '../../../../../common/utils/stub-util';
import {ConsentUtil} from '../../../../common/consent-util';
import {ApiHeaders} from '../../../../../api/api.headers';
import {ConsentAuth, UpdateConsentAuthorizationService, PsuAuthRequest} from '../../../../../api';
import {DATA_PATTERN} from "../../../../common/constant/constant";

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
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  public static ROUTE = SharedRoutes.REVIEW;
  accountAccessLevel = AccountAccessLevel;

  public actualDate: string;
  public finTechName: string;
  public aspspName: string;
  public aisConsent: AisConsentToGrant;

  private authorizationId: string;

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe((res) => {
      this.authorizationId = res.authId;
      this.aspspName = this.sessionService.getBankName(res.authId);
      this.finTechName = this.sessionService.getFintechName(res.authId);
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.actualDate = this.getActualDate();
    });
  }

  onRecurringIndicatorChanged(value: boolean) {
    this.aisConsent.consent.recurringIndicator = value;
  }

  onValidUntilChanged(value: string) {
    const pattern = new RegExp(DATA_PATTERN);

    if (value.match(pattern)) {
      const actualDate = new Date(this.actualDate);
      const date = new Date(value);

      if (date >= actualDate) {
        this.aisConsent.consent.validUntil = value;
      } else {
        console.error('Invalid date: ' + value)
      }
    } else {
      console.error('Invalid date format: ' + value)
    }
  }

  onFrequencyPerDayChanged(value: string) {
    const frequency = Number(value);

    if (frequency > 0) {
      this.aisConsent.consent.frequencyPerDay = frequency;
    }
  }

  onConfirm() {
    const body = { extras: this.aisConsent.extras } as PsuAuthRequest;

    if (this.aisConsent) {
      body.consentAuth = { consent: this.aisConsent.consent } as ConsentAuth;
    }

    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationId,
        StubUtil.X_XSRF_TOKEN,
        StubUtil.X_REQUEST_ID,
        this.sessionService.getRedirectCode(this.authorizationId),
        body,
        'response'
      )
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  onBack() {
    this.location.back();
  }

  private getActualDate(): string {
    const result = new Date();
    result.setDate(result.getDate());
    return result.toISOString().split('T')[0];
  }
}
