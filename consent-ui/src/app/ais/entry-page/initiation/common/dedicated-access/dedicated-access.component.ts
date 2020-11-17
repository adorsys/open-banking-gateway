import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Location } from '@angular/common';

import { SharedRoutes } from '../shared-routes';
import { InternalAccountReference } from '../accounts-reference/accounts-reference.component';
import { SessionService } from '../../../../../common/session.service';
import { ConsentUtil } from '../../../../common/consent-util';
import {AccountReference} from "../../../../common/dto/ais-consent";

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

  public finTechName: string;
  public aspspName: string;

  accounts = [new InternalAccountReference()];
  limitedAccountAccessForm: FormGroup;
  wrongIban: boolean;

  private authorizationId: string;

  ngOnInit() {
    this.wrongIban = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.activatedRoute.parent.parent.params.subscribe((res) => {
      this.authorizationId = res.authId;
      this.aspspName = this.sessionService.getBankName(res.authId);
      this.finTechName = this.sessionService.getFintechName(res.authId);
      this.loadDataFromExistingConsent();
    });
  }

  onSelect() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    consentObj.consent.access.availableAccounts = null;
    consentObj.consent.access.allPsd2 = null;

    consentObj.consent.access.accounts = this.accounts.map((it) => DedicatedAccessComponent.toAccountReference(it));
    consentObj.consent.access.balances = this.accounts.map((it) => DedicatedAccessComponent.toAccountReference(it));
    consentObj.consent.access.transactions = this.accounts.map((it) => DedicatedAccessComponent.toAccountReference(it));

    this.sessionService.setConsentObject(this.authorizationId, consentObj);
    this.router.navigate([SharedRoutes.REVIEW], { relativeTo: this.activatedRoute.parent });
  }

  onBack() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    consentObj.consent.access.availableAccounts = null;
    consentObj.consent.access.allPsd2 = null;
    consentObj.consent.access.accounts = null;
    consentObj.consent.access.balances = null;
    consentObj.consent.access.transactions = null;
    this.sessionService.setConsentObject(this.authorizationId, consentObj);

    this.location.back();
  }

  private static toAccountReference(reference: InternalAccountReference): AccountReference {
    const result = {} as AccountReference;
    result.iban = reference.iban;
    result.currency = reference.currency;
    return result;
  }

  private loadDataFromExistingConsent() {
    const consentObj = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
    if (consentObj.consent.access.accounts) {
      this.accounts = [];
      consentObj.consent.access.accounts.forEach((it) => this.accounts.push(it as InternalAccountReference));
    }
  }
}
