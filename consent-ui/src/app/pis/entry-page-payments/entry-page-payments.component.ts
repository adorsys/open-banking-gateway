import { Component, OnInit } from '@angular/core';
import { PaymentsConsentReviewComponent } from '../payments-consent-review/payments-consent-review.component';

@Component({
  selector: 'consent-app-entry-page-payments',
  templateUrl: './entry-page-payments.component.html',
  styleUrls: ['./entry-page-payments.component.scss'],
  standalone: false
})
export class EntryPagePaymentsComponent implements OnInit {
  public static ROUTE = 'entry-payments';

  accountsConsentReviewPage = PaymentsConsentReviewComponent.ROUTE;

  constructor() {}

  ngOnInit() {}
}
