import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as uuid from 'uuid';
import { Subscription } from 'rxjs';
import { ValidatorService } from 'angular-iban';

@Component({
  selector: 'consent-app-account-selector',
  templateUrl: './accounts-reference.component.html',
  styleUrls: ['./accounts-reference.component.scss']
})
export class AccountsReferenceComponent implements OnInit, OnDestroy {
  @Input() targetForm: FormGroup;
  @Input() accounts: AccountReference[];

  private subscriptions = new Map<string, Subscription>();

  constructor() {}

  ngOnInit() {
    this.accounts.forEach((it) => {
      if (!this.targetForm.contains(it.id)) {
        const control = this.addControlToForm(it);
        control.setValue(it.iban);
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subs, id) => subs.unsubscribe());
  }

  addAccount() {
    const account = new AccountReference();
    this.accounts.push(account);
    this.addControlToForm(account);
  }

  removeAccount(account: AccountReference) {
    this.accounts.splice(this.accounts.indexOf(account), 1);
    this.targetForm.removeControl(account.id);
    this.subscriptions[account.id].unsubscribe();
  }

  private addControlToForm(account: AccountReference): FormControl {
    const formControl = new FormControl('', [ValidatorService.validateIban, Validators.required]);
    this.targetForm.addControl(account.id, formControl);
    this.subscriptions[account.id] = formControl.valueChanges.subscribe((it) => (account.iban = it));
    return formControl;
  }
}

export class AccountReference {
  // internally generated unique ID
  id: string;
  iban: string;

  constructor(iban?: string) {
    this.id = 'account-reference:' + uuid.v4();
    this.iban = iban ? iban : '';
  }
}
