import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { AccountReference, AccountReport, TransactionsResponse } from '../../api';

@Component({
  selector: 'app-show-transactions',
  templateUrl: './show-transactions.component.html',
  styleUrls: ['./show-transactions.component.scss']
})
export class ShowTransactionsComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;

  @Input()
  accountId = '';
  @Input()
  bankId = '';

  transactions: AccountReport;
  showTransactions = false;

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {}

  selectAccount(id) {
    this.showTransactions = false;
    this.transactions = null;
    this.accountId = id;
    console.log('ask for transactions for ', this.accountId);
    this.accountsSubscription = this.aisService.getTransactions(this.bankId, this.accountId).subscribe(transactions => {
      this.transactions = transactions;
      this.showTransactions = true;
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
