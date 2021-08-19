import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {SharedRoutes} from '../../common/shared-routes';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SessionService} from '../../../../../common/session.service';
import {AccountAccessLevel, AisConsentToGrant} from '../../../../common/dto/ais-consent';
import {StubUtil} from '../../../../../common/utils/stub-util';
import {ConsentUtil} from '../../../../common/consent-util';
import {ApiHeaders} from '../../../../../api/api.headers';
import {ConsentAuth, UpdateConsentAuthorizationService, PsuAuthRequest} from '../../../../../api';
import {DATA_PATTERN, MAX_FREQUENCY_PER_DAY} from '../../../../common/constant/constant';
import {DateUtil} from '../../../../common/date-util';

@Component({
  selector: 'consent-app-transactions-consent-review',
  templateUrl: './transactions-consent-review.component.html',
  styleUrls: ['./transactions-consent-review.component.scss']
})
export class TransactionsConsentReviewComponent implements OnInit {

  consentReviewForm: FormGroup;

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
      this.actualDate = DateUtil.getActualDate();
    });
    this.createForm();
  }

  onConfirm() {
    if (this.consentReviewForm.invalid) {
      return;
    }

    this.aisConsent.consent.recurringIndicator = this.consentReviewForm.value.recurringIndicator;
    this.aisConsent.consent.validUntil = this.consentReviewForm.value.validUntilDate;
    this.aisConsent.consent.frequencyPerDay = this.consentReviewForm.value.frequencyPerDay;

    this.sessionService.setConsentObject(this.authorizationId, this.aisConsent);

    const body = { extras: this.aisConsent.extras } as PsuAuthRequest;

    if (this.aisConsent) {
      body.consentAuth = { consent: this.aisConsent.consent } as ConsentAuth;
    }

    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationId,
        StubUtil.X_REQUEST_ID,
        this.sessionService.getRedirectCode(this.authorizationId),
        body,
        'response'
      )
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  onBack() {
    this.location.back();
  }

  private createForm() {
    this.consentReviewForm = this.formBuilder.group({
      recurringIndicator: this.aisConsent.consent.recurringIndicator,
      validUntilDate: [
        this.aisConsent.consent.validUntil,
        [
          Validators.required,
          Validators.pattern(DATA_PATTERN),
          DateUtil.isDateNotInThePastValidator()
        ]
      ],
      frequencyPerDay: [
        this.aisConsent.consent.frequencyPerDay,
        [
          Validators.required,
          Validators.min(1),
          Validators.max(MAX_FREQUENCY_PER_DAY)
        ]
      ]
    })
  }

  get validUntilDate() {
    return this.consentReviewForm.get('validUntilDate')
  }

  get frequencyPerDay() {
    return this.consentReviewForm.get('frequencyPerDay')
  }
}
