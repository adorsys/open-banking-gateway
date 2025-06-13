import { Component, Input } from '@angular/core';
import { TransactionDetails } from '../../../api';
import { NgClass, NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-transaction-card',
  templateUrl: './transaction-card.component.html',
  styleUrls: ['../payment-transaction-card.scss'],
  standalone: true,
  imports: [NgClass, NgForOf, NgIf]
})
export class TransactionCardComponent {
  @Input() transactions: TransactionDetails[];
  @Input() isBookedTransaction: boolean;
  @Input() title: string;
  Number = Number;
}
