import { Component, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';

@Component({
  selector: 'consent-app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {
  public static ROUTE = 'consent-result';

  public finTechName = StubUtil.FINTECH_NAME;
  public title = 'Payment was successful';
  public subtitle = 'Paid 100EUR to IBAN12345';
  redirectTo: string;
  constructor() {}

  ngOnInit() {}

  confirm(value: boolean) {
    window.location.href = this.redirectTo;
  }
}
