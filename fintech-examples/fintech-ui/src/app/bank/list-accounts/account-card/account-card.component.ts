import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../../api';

@Component({
  selector: 'app-account-card',
  templateUrl: './account-card.component.html',
  styleUrls: ['./account-card.component.scss']
})
export class AccountCardComponent implements OnInit {
  @Input() account: AccountDetails;
  @Output() eventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() {}

  ngOnInit() {}

  onSubmit(value: boolean) {
    this.eventEmitter.emit(value);
  }

  selectedAccount: string;

  selectAccount(id) {
    this.selectedAccount = id;
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }

  visibleAccountNumber(acc: AccountDetails) {
    return !acc.iban || acc.iban.length === 0 ? acc.bban : acc.iban;
  }
}
