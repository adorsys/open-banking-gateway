import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';
import { Payment } from '../models/consts';
import { ModalCard } from '../models/modalCard.model';
import { SharedModule } from '../common/shared.module';

@Component({
  selector: 'app-redirect-after-payment-denied',
  templateUrl: './redirect-after-payment-denied.component.html',
  styleUrls: ['./redirect-after-payment-denied.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class RedirectAfterPaymentDeniedComponent implements OnInit {
  private redirectCode;
  cardModal: ModalCard = {
    title: 'Request to abort payment was sent',
    description: 'Your payment was denied!',
    imageUrl: 'assets/icons/icons8-network 2.png',
    confirmBtn: true
  };

  constructor(private consentAuthorizationService: ConsentAuthorizationService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.redirectCode = this.route.snapshot.queryParams.redirectCode;
  }

  onSubmit(value: boolean) {
    if (value) {
      this.redirectCode = this.route.snapshot.queryParams.redirectCode;
      this.consentAuthorizationService.fromPayment(Payment.NOT_OK, this.redirectCode);
    }
  }
}
