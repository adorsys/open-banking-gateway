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

  accountsConsentReviewPage = PaymentsConsentReviewComponent.ROUTE;

  constructor() {}

  ngOnInit() {}
}
