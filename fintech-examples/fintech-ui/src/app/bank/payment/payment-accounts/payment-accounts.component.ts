import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';

@Component({
  selector: 'app-list-accounts-for-payment',
  templateUrl: './payment-accounts.component.html',
  styleUrls: ['./payment-accounts.component.scss']
})
export class PaymentAccountsComponent implements OnInit {
  public static ROUTE = 'accounts';
  selectedAccount;
  accounts = [];

  constructor(private storageService: StorageService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.accounts = this.storageService.getLoa();
  }

  onSelectAccount(id) {
    console.log('router navigate to ../account');
    this.selectedAccount = id;
    this.router.navigate(['../account', id], { relativeTo: this.route });
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }
}
