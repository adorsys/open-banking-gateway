import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ConsentAuthorizationService} from '../../api/consentAuthorization.service';

@Component({
  selector: 'consent-app-entry-page-accounts',
  templateUrl: './entry-page-accounts.component.html',
  styleUrls: ['./entry-page-accounts.component.scss']
})
export class EntryPageAccountsComponent implements OnInit {

  public finTechName = 'Awesome FinTech';
  public bankName = 'Adorsys Sandbox';

  public accountAccesses = [

    new AccountAccess(AccountAccessLevel.ALL_ACCOUNTS, 'Allow seeing a list of all your accounts'),
    new AccountAccess(AccountAccessLevel.ALL_ACCOUNTS_WITH_BALANCES, 'Allow seeing a list of all your accounts with balances'),
    new AccountAccess(AccountAccessLevel.FINE_GRAINED, 'Limit access to specific accounts')
  ];
  public selectedAccess = this.accountAccesses[0];

  public accountAccessForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private consentAuthorisation: ConsentAuthorizationService) {
    this.accountAccessForm = this.formBuilder.group({
      accountAccess: ['', Validators.required],
    });
  }

  ngOnInit() {
  }

  handleMethodSelectedEvent(access: AccountAccess) {
    this.selectedAccess = access;
  }

  submitButtonMessage() {
    return this.selectedAccess.id === AccountAccessLevel.FINE_GRAINED ? 'Specify access' : 'Grant access';
  }

  onSubmit() {
  }
}

export class AccountAccess {

  constructor(public id: AccountAccessLevel, public message: string) {
  }
}

export enum AccountAccessLevel {

  ALL_ACCOUNTS = 'ALL_ACCOUNTS',
  ALL_ACCOUNTS_WITH_BALANCES = 'ALL_ACCOUNTS_WITH_BALANCES',
  FINE_GRAINED = 'FINE_GRAINED'
}
