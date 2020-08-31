import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../../api';

@Component({
  selector: 'app-account-card',
  templateUrl: './account-card.component.html',
  styleUrls: ['./account-card.component.scss']
})
export class AccountCardComponent implements OnInit {
  @Input() account: AccountDetails;
  @Output() eventEmitter: EventEmitter<string> = new EventEmitter<string>();

  constructor() {}

  ngOnInit() {}

  getAccountNumber(acc: AccountDetails) {
    return !acc.iban || acc.iban.length === 0 ? acc.bban : acc.iban;
  }

  onSubmit() {
    this.eventEmitter.emit(this.account.resourceId);
  }
}
