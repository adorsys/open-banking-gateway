import { Component, OnInit } from '@angular/core';
import { StubUtil } from '../../common/utils/stub-util';
import { Action } from '../../common/utils/action';

@Component({
  selector: 'consent-app-to-aspsp-page',
  templateUrl: './to-aspsp-page.component.html',
  styleUrls: ['./to-aspsp-page.component.scss']
})
export class ToAspspPageComponent implements OnInit {
  public static ROUTE = 'to-aspsp-redirection';

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;
  public payment = Action.PAYMENT;

  redirectTo: string;

  constructor() {}

  ngOnInit() {}

  onDeny(): void {}
}
