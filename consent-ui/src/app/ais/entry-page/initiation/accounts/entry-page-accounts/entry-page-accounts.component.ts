import { Component, OnInit } from '@angular/core';
import { Access } from '../../common/initial-consent/consent-account-access-selection.component';
import { AccountsConsentReviewComponent } from '../accounts-consent-review/accounts-consent-review.component';
import { DedicatedAccessComponent } from '../../common/dedicated-access/dedicated-access.component';
import { AccountAccessLevel } from '../../../../common/dto/ais-consent';

@Component({
  selector: 'consent-app-entry-page-accounts',
  templateUrl: './entry-page-accounts.component.html',
  styleUrls: ['./entry-page-accounts.component.scss'],
  standalone: false
})
export class EntryPageAccountsComponent implements OnInit {
  public static ROUTE = 'entry-consent-accounts';

  accountAccess = [
    new Access(AccountAccessLevel.ALL_ACCOUNTS, 'Allow seeing a list of all your accounts'),
    new Access(AccountAccessLevel.ALL_ACCOUNTS_WITH_BALANCES, 'Allow seeing a list of all your accounts with balances'),
    new Access(AccountAccessLevel.FINE_GRAINED, 'Limit access to specific accounts')
  ];
  accountsConsentReviewPage = AccountsConsentReviewComponent.ROUTE;
  dedicatedConsentPage = DedicatedAccessComponent.ROUTE;

  constructor() {}

  ngOnInit() {}
}
