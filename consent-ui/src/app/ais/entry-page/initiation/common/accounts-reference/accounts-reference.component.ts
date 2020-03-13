import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {uuid} from "uuidv4";
import {Subscription} from "rxjs";

@Component({
  selector: 'consent-app-account-selector',
  templateUrl: './accounts-reference.component.html',
  styleUrls: ['./accounts-reference.component.scss']
})
export class AccountsReferenceComponent implements OnInit, OnDestroy {

  @Input() targetForm: FormGroup;
  @Input() accounts: AccountReference[];

  private subscriptions = new Map<string, Subscription>();

  constructor() { }

  ngOnInit() {
    this.accounts.forEach(it => {
      if (!this.targetForm.contains(it.id)) {
        this.addControlToForm(it);
      }
    })
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

  private addControlToForm(account: AccountReference) {
    const formControl = new FormControl('', [Validators.required, Validators.minLength(5)]);
    this.targetForm.addControl(account.id, formControl);
    this.subscriptions[account.id] = formControl.valueChanges.subscribe(it => account.iban = it);
  }
}

export class AccountReference {
  // internally generated unique ID
  id: string;
  iban: string;

  constructor(iban?: string) {
    this.id = "account-reference:" + uuid();
    this.iban = iban ? iban : '';
  }
}
