import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../common/session.service';

import { AuthConsentState } from '../../ais/common/dto/auth-state';
import { SinglePayment } from '../../api';

@Component({
  selector: 'consent-app-payment-info',
  templateUrl: './payment-info.component.html',
  styleUrls: ['./payment-info.component.scss'],
  standalone: false
})
export class PaymentInfoComponent implements OnInit {
  public singlePayment?: SinglePayment;
  private authorizationId: string;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private sessionService: SessionService) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe((res) => {
      this.authorizationId = res.authId;
      this.loadPaymentState();
    });

    this.router.events.subscribe(() => {
      this.loadPaymentState();
    });
  }

  private loadPaymentState(): void {
    const paymentState = this.sessionService.getPaymentState(this.authorizationId, () => new AuthConsentState());
    if (paymentState) {
      this.singlePayment = paymentState.singlePayment;
    }
  }
}
