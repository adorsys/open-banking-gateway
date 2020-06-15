import { Component, OnInit } from '@angular/core';
import { DedicatedAccessComponent } from '../../ais/entry-page/initiation/common/dedicated-access/dedicated-access.component';
import { PaymentsConsentReviewComponent } from '../payments-consent-review/payments-consent-review.component';
import { AccountAccessLevel } from '../../ais/common/dto/ais-consent';
import { Access } from '../../ais/entry-page/initiation/common/initial-consent/consent-account-access-selection.component';

@Component({
  selector: 'consent-app-entry-page-payments',
  templateUrl: './entry-page-payments.component.html',
  styleUrls: ['./entry-page-payments.component.scss']
})
export class EntryPagePaymentsComponent implements OnInit {
  public static ROUTE = 'entry-payments';

  accountAccess = [
    new Access(AccountAccessLevel.ALL_ACCOUNTS, 'Allow seeing a list of all your accounts'),
    new Access(AccountAccessLevel.ALL_ACCOUNTS_WITH_BALANCES, 'Allow seeing a list of all your accounts with balances'),
    new Access(AccountAccessLevel.FINE_GRAINED, 'Limit access to specific accounts')
  ];
  accountsConsentReviewPage = PaymentsConsentReviewComponent.ROUTE;
  dedicatedConsentPage = DedicatedAccessComponent.ROUTE;

  constructor() {}

  ngOnInit() {}
}
