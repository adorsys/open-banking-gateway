import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../../api';
import { NgClass, NgIf } from '@angular/common';

@Component({
  selector: 'app-account-card',
  templateUrl: './account-card.component.html',
  styleUrls: ['./account-card.component.scss'],
  standalone: true,
  imports: [NgClass, NgIf]
})
export class AccountCardComponent implements OnInit {
  @Input() account: AccountDetails;
  @Output() eventEmitter: EventEmitter<string> = new EventEmitter<string>();
  internalAccount: InternalAccount = new InternalAccount();
  Number = Number;

  ngOnInit() {
    this.internalAccount = { accountNumber: this.getAccountNumber(), name: this.account.name };
    const accountBalance = this.account.balances;

    if (accountBalance) {
      this.internalAccount.amount = accountBalance[0].balanceAmount.amount;
      this.internalAccount.currency = accountBalance[0].balanceAmount.currency;
    }
  }

  public getAccountNumber(): string {
    return !this.account.iban || this.account.iban.length === 0 ? this.account.bban : this.account.iban;
  }

  onSubmit() {
    this.eventEmitter.emit(this.account.resourceId);
  }
}

export class InternalAccount {
  name: string;
  accountNumber: string;
  amount?: string;
  currency?: string;
}
