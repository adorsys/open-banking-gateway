import { Component, Input, OnInit } from '@angular/core';
import { TransactionDetails } from '../../../api';

@Component({
  selector: 'app-transaction-card',
  templateUrl: './transaction-card.component.html',
  styleUrls: ['../payment-transaction-card.scss']
})
export class TransactionCardComponent implements OnInit {
  @Input() transactions: Array<TransactionDetails>;
  @Input() isBookedTransaction: boolean;
  @Input() title: string;

  constructor() {}

  ngOnInit() {}
}
