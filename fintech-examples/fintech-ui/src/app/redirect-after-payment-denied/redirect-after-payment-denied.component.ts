import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';
import { Consent, Payment } from '../models/consts';

@Component({
  selector: 'app-redirect-after-payment-denied',
  templateUrl: './redirect-after-payment-denied.component.html',
  styleUrls: ['./redirect-after-payment-denied.component.scss']
})
export class RedirectAfterPaymentDeniedComponent implements OnInit {
  private redirectCode;

  constructor(
    private consentAuthorizationService: ConsentAuthorizationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.redirectCode = this.route.snapshot.queryParams.redirectCode;
  }

  submit() {
    this.redirectCode = this.route.snapshot.queryParams.redirectCode;
    this.consentAuthorizationService.fromPayment(Payment.NOT_OK, this.redirectCode);
  }
}
