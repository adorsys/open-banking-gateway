import { Component, Input, OnInit } from '@angular/core';
import { PaymentInitiationWithStatusResponse } from '../../../api';

@Component({
  selector: 'app-payment-card',
  templateUrl: './payment-card.component.html',
  styleUrls: ['../payment-transaction-card.scss']
})
export class PaymentCardComponent implements OnInit {
  @Input() payments: Array<PaymentInitiationWithStatusResponse>;

  constructor() {}

  ngOnInit() {}
}
