import { Component, OnInit } from '@angular/core';
import { Access } from '../../common/initial-consent/consent-account-access-selection.component';
import { TransactionsConsentReviewComponent } from '../transactions-consent-review/transactions-consent-review.component';
import { DedicatedAccessComponent } from '../../common/dedicated-access/dedicated-access.component';
import { AccountAccessLevel } from '../../../../common/dto/ais-consent';
import {CustomConsentComponent} from "../../common/custom-consent/custom-consent.component";

@Component({
  selector: 'consent-app-entry-page-transactions',
  templateUrl: './entry-page-transactions.component.html',
  styleUrls: ['./entry-page-transactions.component.scss']
})
export class EntryPageTransactionsComponent implements OnInit {
  public static ROUTE = 'entry-consent-transactions';

  transactionsAccountAccess = [
    new Access(AccountAccessLevel.ALL_PSD2, 'Allow seeing a list of all your accounts and transactions'),
    new Access(AccountAccessLevel.FINE_GRAINED, 'Limit access to specific accounts (details and transactions)'),
    new Access(AccountAccessLevel.CUSTOM, '[Technical] Custom consent object')
  ];
  transactionsConsentReviewPage = TransactionsConsentReviewComponent.ROUTE;
  dedicatedConsentPage = DedicatedAccessComponent.ROUTE;
  customConsentPage = CustomConsentComponent.ROUTE;

  constructor() {}

  ngOnInit() {}
}
