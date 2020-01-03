import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {AccountReferenceComponent} from "../account-reference-selector/account-reference.component";

@Component({
  selector: 'app-account-selector',
  templateUrl: './account-selector.component.html',
  styleUrls: ['./account-selector.component.css']
})
export class AccountSelectorComponent implements OnInit {

  @Input() form: FormGroup;

  accounts: AccountReferenceComponent[] = [];
  balances: AccountReferenceComponent[] = [];
  transactions: AccountReferenceComponent[] = [];

  constructor() {}

  ngOnInit() {
  }

  addAccount() {
    this.accounts.push(AccountReferenceComponent.buildWithId(this.accounts.length));
  }

  removeAccount(acc: AccountReferenceComponent) {
    this.accounts = this.accounts.filter(it => it.elemId != acc.elemId);
  }

  addBalance() {
    this.balances.push(AccountReferenceComponent.buildWithId(this.balances.length));
  }

  removeBalance(acc: AccountReferenceComponent) {
    this.balances = this.balances.filter(it => it.elemId != acc.elemId);
  }

  addTransaction() {
    this.transactions.push(AccountReferenceComponent.buildWithId(this.transactions.length));
  }

  removeTransaction(acc: AccountReferenceComponent) {
    this.transactions = this.transactions.filter(it => it.elemId != acc.elemId);
  }
}
