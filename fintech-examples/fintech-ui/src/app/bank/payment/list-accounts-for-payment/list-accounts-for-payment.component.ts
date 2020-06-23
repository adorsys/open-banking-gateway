import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../../services/storage.service';
import { AccountStruct } from '../../redirect-page/redirect-struct';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-list-accounts-for-payment',
  templateUrl: './list-accounts-for-payment.component.html',
  styleUrls: ['./list-accounts-for-payment.component.scss']
})
export class ListAccountsForPaymentComponent implements OnInit {
  public static ROUTE = 'loa';
  selectedAccount;
  accounts: AccountStruct[] = [];

  constructor(private storageService: StorageService,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.accounts = this.storageService.getLoa();
  }

  selectAccount(id) {
    this.selectedAccount = id;
    this.router.navigate(['../accounts'], { relativeTo: this.route });

  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }

}
