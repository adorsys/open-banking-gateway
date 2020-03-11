import { Component, OnDestroy, OnInit } from '@angular/core';
import { AccountDetails } from '../../api';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html'
})
export class ListAccountsComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;
  accounts: AccountDetails[];
  selectedAccount: string;
  bankId = '';

  constructor(private route: ActivatedRoute, private aisService: AisService) {
    this.bankId = route.parent.snapshot.paramMap.get('bankid');
    console.log('list-accounts', this.accounts);
  }

  ngOnInit() {
    this.accountsSubscription = this.aisService.getAccounts(this.bankId).subscribe(accounts => {
      this.accounts = accounts;
      console.log('list-accounts2', this.accounts);
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }

  selectAccount(id) {
    this.selectedAccount = id;
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }
}
