import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';

@Component({
  selector: 'app-show-transactions',
  templateUrl: './show-transactions.component.html',
  styleUrls: ['./show-transactions.component.scss']
})
export class ShowTransactionsComponent implements OnInit {
  @Input()
  accountId = '';
  @Input()
  bankId = '';

  private transactions;
  showTransactions = false;

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {}

  selectAccount(id) {
    this.accountId = id;
    console.log('ask for transactions for ', this.accountId);
    /*
    this.aisService.getTransactions(this.bankId, this.accountId).subscribe(transactions => {
        this.transactions = transactions;
        this.showTransactions = true;
      }
    );
     */
  }
}
