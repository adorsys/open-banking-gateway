import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { AccountReport, TransactionDetails } from '../../api';
import { RedirectStruct } from '../redirect-page/redirect-struct';

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

  constructor(private router: Router, private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    this.route.parent.parent.parent.paramMap.subscribe(p => {
      this.bankId = p.get('bankid');
      console.log('list-transactions for bankid', this.bankId);
    });

    this.route.paramMap.subscribe(p => {
      this.accountId = p.get('accountid');
      console.log('list-transactions for accountid', this.accountId);
      // when accounts param is found, banks param must have been found before
      // because bank param is earlier in path
      this.askForTransactions();
    });
  }

  askForTransactions() {
    this.transactionsLists = [];
    console.log('ON INIT LTX ask for transactions for ', this.accountId);
    this.accountsSubscription = this.aisService.getTransactions(this.bankId, this.accountId).subscribe(response => {
      switch (response.status) {
        case 202:
          console.log('list tx got REDIRECT');
          const location = encodeURIComponent(response.headers.get('location'));
          const r = new RedirectStruct();
          r.okUrl = location;
          r.cancelUrl = '../..';
          this.router.navigate(['../redirect', JSON.stringify(r)], { relativeTo: this.route });
          break;
        case 200:
          console.log('I got transactions');
          this.transactionsLists = [response.body.transactions.pending, response.body.transactions.booked];
          this.makeVisible = true;
      }
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
