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
  private showTransactions = false;

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    console.log('accountId:' + this.accountId + ' and bankid ' + this.bankId);
    if (this.accountId !== '') {
      console.log('ask for transactions');
      /*
      this.aisService.getTransactions(this.bankId, this.accountId).subscribe(transactions => {
          this.transactions = transactions;
          this.showTransactions = true;
        }
      );

       */
    }
  }
}
