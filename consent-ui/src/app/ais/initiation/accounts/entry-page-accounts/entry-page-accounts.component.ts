import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from "@angular/router";
import {StubUtil} from "../../../common/stub-util";
import {SessionService} from "../../../../common/session.service";
import {ConsentUtil} from "../../../common/consent-util";
import {AccountAccess, AccountAccessLevel, AisConsent, AisConsentToGrant} from "../../../common/dto/ais-consent";
import {AccountsConsentReviewComponent} from "../accounts-consent-review/accounts-consent-review.component";
import {AuthConsentState} from "../../../common/dto/auth-state";
import {DedicatedAccessComponent} from "../../common/dedicated-access/dedicated-access.component";

@Component({
  selector: 'consent-app-entry-page-accounts',
  templateUrl: './entry-page-accounts.component.html',
  styleUrls: ['./entry-page-accounts.component.scss']
})
export class EntryPageAccountsComponent implements OnInit {

  public static ROUTE = 'entry-consent-accounts';

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  public accountAccesses = [
    new Access(AccountAccessLevel.ALL_ACCOUNTS, 'Allow seeing a list of all your accounts'),
    new Access(AccountAccessLevel.ALL_ACCOUNTS_WITH_BALANCES, 'Allow seeing a list of all your accounts with balances'),
    new Access(AccountAccessLevel.FINE_GRAINED, 'Limit access to specific accounts')
  ];
  public selectedAccess = new FormControl(this.accountAccesses[0], Validators.required);
  public accountAccessForm: FormGroup;

  private authorizationId: string;
  private state: AuthConsentState;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService
  ) {
    this.accountAccessForm = this.formBuilder.group({});
    this.accountAccessForm.addControl('accountAccess', this.selectedAccess);
  }

  ngOnInit() {
    this.activatedRoute.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.state = this.sessionService.getConsentState(this.authorizationId, () => new AuthConsentState());
      if (!this.hasInputs()) {
        this.moveToReviewConsent();
      }
    })
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
      consentObj.consent.access.availableAccounts = this.selectedAccess.value.id;
    }

    this.sessionService.setConsentObject(this.authorizationId, consentObj);
  }

  private handleGenericAccess() {
    this.moveToReviewConsent();
  }

  private handleDedicatedAccess() {
    this.router.navigate([DedicatedAccessComponent.ROUTE], {relativeTo: this.activatedRoute.parent});
    return;
  }

  private moveToReviewConsent() {
    this.router.navigate([AccountsConsentReviewComponent.ROUTE], {relativeTo: this.activatedRoute.parent});
  }
}

export class Access {

  constructor(public id: AccountAccessLevel, public message: string) {
  }
}
