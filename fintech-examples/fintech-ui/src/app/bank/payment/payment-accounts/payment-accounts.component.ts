import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';
import { Consts } from '../../../models/consts';

@Component({
  selector: 'app-list-accounts-for-payment',
  templateUrl: './payment-accounts.component.html',
  styleUrls: ['./payment-accounts.component.scss']
})
export class PaymentAccountsComponent implements OnInit {
  public static ROUTE = 'accounts';
  selectedAccount;
  bankId: string;
  accounts = [];

  constructor(private storageService: StorageService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.params[Consts.BANK_ID_NAME];
    this.accounts = this.storageService.getLoa(this.bankId);
  }

  onSelectAccount(id) {
    console.log('router navigate to ../account');
    this.selectedAccount = id;
    this.router.navigate(['../account', id], { relativeTo: this.route });
  }
}
