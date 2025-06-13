import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { AuthStateConsentAuthorizationService, ConsentAuth } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { AuthConsentState } from '../../ais/common/dto/auth-state';
import { EntryPagePaymentsComponent } from '../entry-page-payments/entry-page-payments.component';

@Component({
  selector: 'consent-app-initiation',
  templateUrl: './payment-initiate.component.html',
  styleUrls: ['./payment-initiate.component.scss'],
  standalone: false
})
export class PaymentInitiateComponent implements OnInit {
  private redirectCode: string;
  private route: ActivatedRouteSnapshot;
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sessionService: SessionService,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService
  ) {}

  private static isInvalid(authorizationId: string, redirectCode: string): boolean {
    return !redirectCode || !authorizationId || '' === redirectCode || '' === authorizationId;
  }

  ngOnInit() {
    this.route = this.activatedRoute.snapshot;

    const authId = this.route.params.authId;
    this.redirectCode = this.route.queryParams.redirectCode;
    if (this.redirectCode) {
      this.sessionService.setRedirectCode(authId, this.redirectCode);
    }

    if (PaymentInitiateComponent.isInvalid(authId, this.redirectCode)) {
      this.abortUnauthorized();
    } else {
      this.initiatePaymentSession(authId, this.redirectCode);
    }
  }

  private abortUnauthorized() {
    this.router.navigate(['./error'], { relativeTo: this.activatedRoute.parent });
  }

  private initiatePaymentSession(authorizationId: string, redirectCode: string) {
    this.authStateConsentAuthorizationService
      .authUsingGET(authorizationId, redirectCode, 'response')
      .subscribe((res) => {
        this.sessionService.setRedirectCode(authorizationId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));

        // setting bank and fintech names
        this.sessionService.setBankName(authorizationId, (res.body as ConsentAuth).bankName);
        this.sessionService.setFintechName(authorizationId, (res.body as ConsentAuth).fintechName);

        this.navigate(authorizationId, res.body);
      });
  }

  private navigate(authorizationId: string, res: ConsentAuth) {
    this.sessionService.setPaymentState(authorizationId, new AuthConsentState(res.violations, res.singlePayment));
    this.router.navigate([EntryPagePaymentsComponent.ROUTE], { relativeTo: this.activatedRoute.parent });
  }
}
