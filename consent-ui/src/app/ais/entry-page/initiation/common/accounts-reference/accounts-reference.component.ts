import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import * as uuid from 'uuid';
import { Subscription } from 'rxjs';
import { ValidatorService } from 'angular-iban';
import {DEFAULT_CURRENCY} from '../../../../common/constant/constant';

@Component({
  selector: 'consent-app-account-selector',
  templateUrl: './accounts-reference.component.html',
  styleUrls: ['./accounts-reference.component.scss']
})
export class AccountsReferenceComponent implements OnInit, OnDestroy {
  @Input() targetForm: FormGroup;
  @Input() accounts: InternalAccountReference[];

  private subscriptions = new Map<string, Subscription>();

  constructor() {}

  ngOnInit() {
    this.accounts.forEach((it) => {
      if (!(this.targetForm.contains(it.ibanId) && this.targetForm.contains(it.currencyId))) {
        const [ibanControl, currencyControl] = this.addControlToForm(it);
        ibanControl.setValue(it.iban);
        currencyControl.setValue(it.currency);
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subs, id) => subs.unsubscribe());
  }

  addAccount() {
    const account = new InternalAccountReference();
    this.accounts.push(account);
    this.addControlToForm(account);
  }

  removeAccount(account: InternalAccountReference) {
    this.accounts.splice(this.accounts.indexOf(account), 1);
    this.targetForm.removeControl(account.ibanId);
    this.targetForm.removeControl(account.currencyId);
    this.subscriptions[account.ibanId].unsubscribe();
    this.subscriptions[account.currencyId].unsubscribe();
  }

  private addControlToForm(account: InternalAccountReference) : FormControl[] {
    if (!(account.ibanId && account.currencyId)) {
      const id = InternalAccountReference.generateId();
      account.ibanId = InternalAccountReference.generateIbanId(id);
      account.currencyId = InternalAccountReference.generateCurrencyId(id);
    }

    const ibanFormControl = new FormControl('', [ValidatorService.validateIban, Validators.required]);
    this.targetForm.addControl(account.ibanId, ibanFormControl);

    const currencyFormControl = new FormControl(DEFAULT_CURRENCY, Validators.required);
    this.targetForm.addControl(account.currencyId, currencyFormControl );

    this.subscriptions[account.ibanId] = ibanFormControl.valueChanges.subscribe((it) => (account.iban = it));
    this.subscriptions[account.currencyId] = currencyFormControl.valueChanges.subscribe((it) => (account.currency = it));

    return [ibanFormControl, currencyFormControl];
  }
}

export class InternalAccountReference {
  // internally generated unique ID
  ibanId: string;
  currencyId: string;
  iban: string;
  currency: string;

  constructor(iban?: string, currency?: string) {
    const id = InternalAccountReference.generateId();
    this.ibanId = InternalAccountReference.generateIbanId(id);
    this.currencyId = InternalAccountReference.generateCurrencyId(id);
    this.iban = iban ? iban : '';
    this.currency = currency ? currency : DEFAULT_CURRENCY;
  }

  static generateId(): string {
    return 'account-reference:' + uuid.v4();
  }

  static generateIbanId(id: string): string {
    return id + '-iban';
  }

  static generateCurrencyId(id: string): string {
    return id + '-currency';
  }
}
