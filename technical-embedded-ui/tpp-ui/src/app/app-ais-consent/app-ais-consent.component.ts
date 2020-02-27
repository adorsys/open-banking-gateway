import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-app-ais-consent',
  templateUrl: './app-ais-consent.component.html',
  styleUrls: ['./app-ais-consent.component.css']
})
export class AppAisConsentComponent implements OnInit {

  @Input() aisConsent: AisConsentBody;
  @Input() form: FormGroup;

  constructor() { }

  ngOnInit() {
  }
}

export class AisConsentBody {

  access: AccountAccessBody;
  recurringIndicator = false;
  validUntil: string;
  frequencyPerDay: number;
}

export class AccountAccessBody {

  accounts: string[];
  balances: string[];
  transactions: string[];

  availableAccounts: string;
  allPsd2: string;
}
