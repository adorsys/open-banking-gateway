import { Component, Input, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthConsentState } from '../../ais/common/dto/auth-state';
import { AccountAccessLevel, AisConsentToGrant } from '../../ais/common/dto/ais-consent';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { DenyRequest, UpdateConsentAuthorizationService } from '../../api';
import { ConsentUtil } from '../../ais/common/consent-util';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-payment-access-selection',
  templateUrl: './consent-payment-access-selection.component.html',
  styleUrls: ['./consent-payment-access-selection.component.scss']
})
export class ConsentPaymentAccessSelectionComponent implements OnInit {
  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  @Input() accountAccesses: Access[];
  @Input() consentReviewPage: string;
  @Input() dedicatedConsentPage: string;

  public selectedAccess;
  public accountAccessForm: FormGroup;
  public state: AuthConsentState;
  public consent: AisConsentToGrant;

  private authorizationId: string;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {
    this.accountAccessForm = this.formBuilder.group({});
  }

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.state = this.sessionService.getConsentState(this.authorizationId, () => new AuthConsentState());
      if (!this.hasInputs()) {
        this.moveToReviewConsent();
      }

      this.selectedAccess = new FormControl(this.accountAccesses[0], Validators.required);
      this.accountAccessForm.addControl('accountAccess', this.selectedAccess);
      this.consent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    });
  }

  hasInputs(): boolean {
    return this.hasAisViolations() || this.hasGeneralViolations();
  }

  hasAisViolations(): boolean {
    return this.state.hasAisViolation();
  }

  hasGeneralViolations(): boolean {
    return this.state.hasGeneralViolation();
  }

  handleMethodSelectedEvent(access: Access) {
    this.selectedAccess.setValue(access);
    console.log(this.selectedAccess);
  }

  submitButtonMessage() {
    return this.selectedAccess.value.id === AccountAccessLevel.FINE_GRAINED ? 'Specify access' : 'Grant access';
  }

  onConfirm() {
    this.updateConsentObject();

    if (this.selectedAccess.value.id === AccountAccessLevel.FINE_GRAINED) {
      this.handleDedicatedAccess();
      return;
    }

    this.handleGenericAccess();
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

  private updateConsentObject() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    if (this.state.hasGeneralViolation()) {
      consentObj.extras = consentObj.extras ? consentObj.extras : {};
      this.state
        .getGeneralViolations()
        .forEach(it => (consentObj.extras[it.code] = this.accountAccessForm.get(it.code).value));
    }

    consentObj.level = this.selectedAccess.value.id;

    if (this.selectedAccess.value.id !== AccountAccessLevel.FINE_GRAINED) {
      if (this.selectedAccess.value.id === AccountAccessLevel.ALL_PSD2) {
        consentObj.consent.access.allPsd2 = AccountAccessLevel.ALL_ACCOUNTS;
      } else {
        consentObj.consent.access.availableAccounts = this.selectedAccess.value.id;
      }
    }

    this.sessionService.setConsentObject(this.authorizationId, consentObj);
  }

  private handleGenericAccess() {
    this.moveToReviewConsent();
  }

  private handleDedicatedAccess() {
    this.router.navigate([this.dedicatedConsentPage], { relativeTo: this.activatedRoute });
    return;
  }

  private moveToReviewConsent() {
    this.router.navigate([this.consentReviewPage], { relativeTo: this.activatedRoute });
  }
}

export class Access {
  constructor(public id: AccountAccessLevel, public message: string) {}
}
