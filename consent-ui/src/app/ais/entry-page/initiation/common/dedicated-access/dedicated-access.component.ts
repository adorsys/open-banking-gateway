import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SharedRoutes } from '../shared-routes';
import { StubUtil } from '../../../../common/stub-util';
import { AccountReference } from '../accounts-reference/accounts-reference.component';
import { SessionService } from '../../../../../common/session.service';
import { ConsentUtil } from '../../../../common/consent-util';
import {Location} from '@angular/common';

@Component({
  selector: 'consent-app-limited-access',
  templateUrl: './dedicated-access.component.html',
  styleUrls: ['./dedicated-access.component.scss']
})
export class DedicatedAccessComponent implements OnInit {
  constructor(
    private location: Location,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private sessionService: SessionService
  ) {
    this.limitedAccountAccessForm = this.formBuilder.group({});
  }

  public static ROUTE = 'dedicated-account-access';

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  accounts = [new AccountReference()];
  limitedAccountAccessForm: FormGroup;

  private authorizationId: string;

  ngOnInit() {
    this.activatedRoute.parent.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
    });
  }

  onSelect() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    consentObj.consent.access.availableAccounts = null;
    consentObj.consent.access.allPsd2 = null;

    consentObj.consent.access.accounts = this.accounts.map(it => it.iban);
    consentObj.consent.access.balances = this.accounts.map(it => it.iban);
    consentObj.consent.access.transactions = this.accounts.map(it => it.iban);

    this.sessionService.setConsentObject(this.authorizationId, consentObj);
    this.router.navigate([SharedRoutes.REVIEW], { relativeTo: this.activatedRoute.parent });
  }

  onBack() {
    this.location.back();
  }
}
