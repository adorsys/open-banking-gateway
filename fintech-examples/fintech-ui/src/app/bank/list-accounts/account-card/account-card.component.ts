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

  isSelected(id) {
    return id === this.account.resourceId ? 'selected' : 'unselected';
  }

  visibleAccountNumber(acc: AccountDetails) {
    return !acc.iban || acc.iban.length === 0 ? acc.bban : acc.iban;
  }

  onSubmit(value: boolean) {
    if (value) {
      this.eventEmitter.emit(value);
    }
  }
}
