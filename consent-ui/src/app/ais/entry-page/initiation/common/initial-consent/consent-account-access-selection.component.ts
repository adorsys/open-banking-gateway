import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthConsentState} from "../../../../common/dto/auth-state";
import {SessionService} from "../../../../../common/session.service";
import {StubUtil} from "../../../../common/stub-util";
import {AccountAccessLevel} from "../../../../common/dto/ais-consent";
import {ConsentUtil} from "../../../../common/consent-util";

@Component({
  selector: 'consent-app-access-selection',
  templateUrl: './consent-account-access-selection.component.html',
  styleUrls: ['./consent-account-access-selection.component.scss']
})
export class ConsentAccountAccessSelectionComponent implements OnInit {

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  @Input() accountAccesses: Access[];
  @Input() consentReviewPage: string;
  @Input() dedicatedConsentPage: string;

  public selectedAccess;
  public accountAccessForm: FormGroup;
  public state: AuthConsentState;

  private authorizationId: string;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService
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
    });

    this.selectedAccess = new FormControl(this.accountAccesses[0], Validators.required);
    this.accountAccessForm.addControl('accountAccess', this.selectedAccess);
  }

  hasInputs(): boolean {
    return this.hasAisViolations() || this.hasGeneralViolations()
  }

  hasAisViolations(): boolean {
    return this.state.hasAisViolation()
  }

  hasGeneralViolations(): boolean {
    return this.state.hasGeneralViolation()
  }

  handleMethodSelectedEvent(access: Access) {
    this.selectedAccess.setValue(access);
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

  private updateConsentObject() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    if (this.state.hasGeneralViolation()) {
      consentObj.extras = consentObj.extras ? consentObj.extras : {};
      this.state.getGeneralViolations()
        .forEach(it => consentObj.extras[it.code] = this.accountAccessForm.get(it.code).value)
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
    this.router.navigate([this.dedicatedConsentPage], {relativeTo: this.activatedRoute});
    return;
  }

  private moveToReviewConsent() {
    this.router.navigate([this.consentReviewPage], {relativeTo: this.activatedRoute});
  }
}

export class Access {

  constructor(public id: AccountAccessLevel, public message: string) {
  }
}
