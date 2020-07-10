import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../common/session.service';

import { AuthConsentState } from '../../ais/common/dto/auth-state';
import { SinglePayment } from '../../api';

@Component({
  selector: 'consent-app-payment-info',
  templateUrl: './payment-info.component.html',
  styleUrls: ['./payment-info.component.scss']
})
export class PaymentInfoComponent implements OnInit {
  private authorizationId: string;
  public singlePayment?: SinglePayment;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private sessionService: SessionService) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.loadPaymentState();
    });
  }

  private loadPaymentState(): void {
    setTimeout(() => {
      const tmp = this.sessionService.getPaymentState(this.authorizationId, () => new AuthConsentState());
      console.log(tmp);
      this.singlePayment = tmp.singlePayment;
    }, 500);
  }
}
