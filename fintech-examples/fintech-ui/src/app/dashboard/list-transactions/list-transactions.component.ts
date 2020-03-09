import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { AccountReport } from '../../api';

@Component({
  selector: 'app-list-transactions',
  templateUrl: './list-transactions.component.html'
})
export class ListTransactionsComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;

  @Input()
  accountId = '';
  @Input()
  bankId = '';

  transactions: AccountReport;
  makeVisible = false;

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {}

  selectAccount(id) {
    this.makeVisible = false;
    this.transactions = null;
    this.accountId = id;
    console.log('ask for transactions for ', this.accountId);
    this.accountsSubscription = this.aisService.getTransactions(this.bankId, this.accountId).subscribe(transactions => {
      this.transactions = transactions;
      this.makeVisible = true;
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
