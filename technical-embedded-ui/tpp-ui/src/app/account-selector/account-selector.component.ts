import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {AccountReferenceComponent} from "../account-reference-selector/account-reference.component";
import {Globals} from "../globals";
import {AccountAccessBody, AisConsentBody} from "../app-ais-consent/app-ais-consent.component";

@Component({
  selector: 'app-account-selector',
  templateUrl: './account-selector.component.html',
  styleUrls: ['./account-selector.component.css']
})
export class AccountSelectorComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() aisConsent: AisConsentBody;

  consentLevel = ConsentLevel;

  accessSelector = new FormControl();
  allAccounts = new FormControl();
  allPsd2 = new FormControl();

  constructor(private globals: Globals) {}

  ngOnInit() {
    this.aisConsent.access = new AccountAccessBody();
    this.form.addControl('aisConsent.access.allAccountsAccess', this.allAccounts);
    this.form.addControl('aisConsent.access.allPsd2', this.allPsd2);

    this.globals.userInfo.subscribe(it => {
      if (it.id === 'ais.allAccounts') {
        this.clearConsent();
        this.accessSelector.setValue(ConsentLevel.GLOBAL_ACCOUNTS);
        this.aisConsent.access.availableAccounts = it.value;
      }

      if (it.id === 'ais.accounts') {
        this.clearConsent();
        this.accessSelector.setValue(ConsentLevel.DEDICATED);
        const acc = this.addAccount();
        this.aisConsent.access.accounts[acc] = it.value;
      }

      if (it.id === 'ais.balances') {
        this.clearConsent();
        this.accessSelector.setValue(ConsentLevel.DEDICATED);
        const bal = this.addBalance();
        this.aisConsent.access.balances[bal] = it.value;
      }

      if (it.id === 'ais.transactions') {
        this.clearConsent();
        this.accessSelector.setValue(ConsentLevel.DEDICATED);
        const txn = this.addTransaction();
        this.aisConsent.access.transactions[txn] = it.value;
      }

      if (it.id === 'ais.recurringIndicator') {
        this.aisConsent.recurringIndicator = it.value;
      }

      if (it.id === 'ais.frequencyPerDay') {
        this.aisConsent.frequencyPerDay = it.value;
      }

      if (it.id === 'ais.validUntil') {
        this.aisConsent.validUntil = it.value;
      }
      console.log(this.aisConsent)
    });
  }

  clearConsent() {
    this.aisConsent.access.allPsd2 = null;
    this.aisConsent.access.availableAccounts = null;
    this.aisConsent.access.accounts = [];
    this.aisConsent.access.balances = [];
    this.aisConsent.access.transactions = [];
  }

  addAccount() : number {
    if (!this.aisConsent.access.accounts) {
      this.aisConsent.access.accounts = [];
    }

    this.aisConsent.access.accounts.push("");
    return this.aisConsent.access.accounts.length - 1;
  }

  removeAccount(acc: AccountReferenceComponent) {
    this.aisConsent.access.accounts.splice(this.aisConsent.access.accounts.indexOf(acc.ibanValue), 1);
    acc.remove();
  }

  addBalance(): number {
    if (!this.aisConsent.access.balances) {
      this.aisConsent.access.balances = [];
    }

    this.aisConsent.access.balances.push("");
    return this.aisConsent.access.balances.length - 1;
  }

  removeBalance(bal: AccountReferenceComponent) {
    this.aisConsent.access.balances.splice(this.aisConsent.access.balances.indexOf(bal.ibanValue), 1);
    bal.remove();
  }

  addTransaction(): number {
    if (!this.aisConsent.access.transactions) {
      this.aisConsent.access.transactions = [];
    }

    this.aisConsent.access.transactions.push("");
    return this.aisConsent.access.transactions.length - 1;
  }

  removeTransaction(txn: AccountReferenceComponent) {
    this.aisConsent.access.transactions.splice(this.aisConsent.access.transactions.indexOf(txn.ibanValue), 1);
    txn.remove();
  }
}

export enum ConsentLevel {
  DEDICATED = 'DEDICATED',
  GLOBAL_ACCOUNTS = 'GLOBAL_ACCOUNTS',
  GLOBAL_PSD2 = 'GLOBAL_PSD2'
}
