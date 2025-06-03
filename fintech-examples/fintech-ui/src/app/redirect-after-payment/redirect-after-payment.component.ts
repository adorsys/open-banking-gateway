import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';
import { Payment } from '../models/consts';
import { ModalCard } from '../models/modalCard.model';
import { SharedModule } from '../common/shared.module';

@Component({
  selector: 'app-redirect-after-payment',
  templateUrl: './redirect-after-payment.component.html',
  styleUrls: ['./redirect-after-payment.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class RedirectAfterPaymentComponent implements OnInit {
  cardModal: ModalCard = {
    title: 'Payment was successful',
    description: 'Please wait while we load data'
  };

  constructor(private consentAuthorizationService: ConsentAuthorizationService, private route: ActivatedRoute) {}

  ngOnInit() {
    const redirectCode = this.route.snapshot.queryParams.redirectCode;
    this.consentAuthorizationService.fromPayment(Payment.OK, redirectCode);
  }
}
