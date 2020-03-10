import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {uuid} from "uuidv4";

@Component({
  selector: 'consent-app-account-selector',
  templateUrl: './account-selector.component.html',
  styleUrls: ['./account-selector.component.scss']
})
export class AccountSelectorComponent implements OnInit {

  @Input() targetForm: FormGroup;
  @Input() accounts: Account[];

  constructor() { }

  ngOnInit() {
    this.accounts.forEach(it => {
      if (!this.targetForm.contains(it.id)) {
        this.addControlToForm(it);
      }
    })
  }

  addAccount() {
    const account = new Account();
    this.accounts.push(account);
    this.addControlToForm(account);
  }

  removeAccount(account: Account) {
    this.accounts.splice(this.accounts.indexOf(account), 1);
    this.targetForm.removeControl(account.id);
  }

  private addControlToForm(account: Account) {
    this.targetForm.addControl(account.id, new FormControl(account.iban, [Validators.required, Validators.minLength(5)]));
    console.log(this.accounts)
  }

}

export class Account {
  // internally generated unique ID
  id: string;
  iban: string;


  constructor(iban?: string) {
    this.id = "account:" + uuid();
    this.iban = iban ? iban : '';
  }
}
