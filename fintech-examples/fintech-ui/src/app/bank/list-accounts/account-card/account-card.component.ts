import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../../api';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-account-card',
  templateUrl: './account-card.component.html',
  styleUrls: ['./account-card.component.scss']
})
export class AccountCardComponent implements OnInit {
  @Input() account: AccountDetails;
  @Output() eventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {}

  visibleAccountNumber(acc: AccountDetails) {
    return !acc.iban || acc.iban.length === 0 ? acc.bban : acc.iban;
  }

  onSubmit(value: boolean) {
    if (value) {
      this.eventEmitter.emit(value);
    }
  }

  isSelected(id: string): boolean {
    return id == this.route.snapshot.paramMap.get('accountid');
  }
}
