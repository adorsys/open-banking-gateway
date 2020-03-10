import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'consent-app-transactions-consent-review',
  templateUrl: './transactions-consent-review.component.html',
  styleUrls: ['./transactions-consent-review.component.scss']
})
export class TransactionsConsentReviewComponent implements OnInit {

  public static ROUTE = 'review-consent-transactions';

  constructor() { }

  ngOnInit() {
  }

}
