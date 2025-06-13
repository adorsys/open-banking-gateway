import { Component, Input } from '@angular/core';
import { PaymentInitiationWithStatusResponse } from '../../../api';
import { NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-payment-card',
  templateUrl: './payment-card.component.html',
  styleUrls: ['../payment-transaction-card.scss'],
  standalone: true,
  imports: [NgForOf, NgIf]
})
export class PaymentCardComponent {
  @Input() payments: PaymentInitiationWithStatusResponse[];
}
