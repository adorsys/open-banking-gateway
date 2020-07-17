import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';
import { Payment } from '../models/consts';

@Component({
  selector: 'app-redirect-after-payment',
  templateUrl: './redirect-after-payment.component.html',
  styleUrls: ['./redirect-after-payment.component.scss']
})
export class RedirectAfterPaymentComponent implements OnInit {
  constructor(private authService: ConsentAuthorizationService, private route: ActivatedRoute) {}

  ngOnInit() {
    const redirectCode = this.route.snapshot.queryParams.redirectCode;

    this.authService.fromPaymentOk(Payment.OK, redirectCode);
  }
}
