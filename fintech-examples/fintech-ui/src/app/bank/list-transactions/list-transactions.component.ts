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

  accountId = '';
  bankId = '';

  makeVisible = false;
  transactionsLists: Array<Array<TransactionDetails>>;
  transactionsListNames = ['Pending', 'Booked'];

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    this.bankId = this.route.parent.parent.parent.snapshot.paramMap.get('bankid');
    console.log('list-transactions for bankid', this.bankId);
    this.accountId = this.route.snapshot.paramMap.get('accountid');
    console.log('list-transactions for accountid', this.accountId);

    this.transactionsLists = [];
    console.log('ask for transactions for ', this.accountId);
    this.accountsSubscription = this.aisService.getTransactions(this.bankId, this.accountId).subscribe(transactions => {
      this.transactionsLists = [transactions.pending, transactions.booked];
      this.makeVisible = true;
    });
  }

  ngOnDestroy(): void {
    //  this.accountsSubscription.unsubscribe();
  }
}
