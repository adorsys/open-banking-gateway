import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { AccountReport, TransactionDetails } from '../../api';

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

  makeVisible = false;
  transactionsLists: Array<Array<TransactionDetails>>;
  transactionsListNames = ['Pending', 'Booked'];

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {}

  selectAccount(id) {
    this.makeVisible = false;
    this.transactionsLists = [];
    this.accountId = id;
    console.log('ask for transactions for ', this.accountId);
    this.accountsSubscription = this.aisService.getTransactions(this.bankId, this.accountId).subscribe(transactions => {
      this.transactionsLists = [transactions.pending, transactions.booked];
      this.makeVisible = true;
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
