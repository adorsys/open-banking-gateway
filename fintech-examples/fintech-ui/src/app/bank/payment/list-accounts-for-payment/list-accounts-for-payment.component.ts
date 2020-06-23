import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../../services/storage.service';
import { AccountStruct } from '../../redirect-page/redirect-struct';

@Component({
  selector: 'app-list-accounts-for-payment',
  templateUrl: './list-accounts-for-payment.component.html',
  styleUrls: ['./list-accounts-for-payment.component.scss']
})
export class ListAccountsForPaymentComponent implements OnInit {
  public static ROUTE = 'loa';
  selectedAccount;
  accounts: AccountStruct[] = [];

  constructor(private storageService: StorageService) {
  }

  ngOnInit() {
    this.accounts = this.storageService.getLoa();
  }

  selectAccount(id) {
    this.selectedAccount = id;
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }

}
