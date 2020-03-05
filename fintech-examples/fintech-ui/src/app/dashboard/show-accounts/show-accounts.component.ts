import { Component, Input, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../api';

@Component({
  selector: 'app-show-accounts',
  templateUrl: './show-accounts.component.html',
  styleUrls: ['./show-accounts.component.scss']
})
export class ShowAccountsComponent implements OnInit {
  @Input()
  bankId = '';

  @Output()
  selectedAccount = '';

  config = {
    headline: 'small',
    subheadline: 'large',
    shadow: 'shadow'
  };

  @Input()
  accounts: AccountDetails[];

  constructor() {}

  ngOnInit() {}

  selectAccount(id) {
    console.log('selected account:', id);
    this.selectedAccount = id;
  }
}
