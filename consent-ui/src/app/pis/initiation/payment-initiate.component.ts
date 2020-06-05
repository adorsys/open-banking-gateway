import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from "@angular/router";
import {SessionService} from "../../common/session.service";
import {AuthStateConsentAuthorizationService, ConsentAuth} from "../../api";
import {ApiHeaders} from "../../api/api.headers";
import {AuthConsentState} from "../../ais/common/dto/auth-state";
import {EntryPagePaymentsComponent} from "../entry-page-payments/entry-page-payments.component";

@Component({
  selector: 'consent-app-initiation',
  templateUrl: './payment-initiate.component.html',
  styleUrls: ['./payment-initiate.component.scss']
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
      this.initiateConsentSession(authId, this.redirectCode);
    }
  }

  private abortUnauthorized() {
    this.router.navigate(['./error'], { relativeTo: this.activatedRoute.parent });
  }

  private initiateConsentSession(authorizationId: string, redirectCode: string) {
    this.authStateConsentAuthorizationService
      .authUsingGET(authorizationId, redirectCode, 'response')
      .subscribe(res => {
        this.sessionService.setRedirectCode(authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        this.navigate(authorizationId, res.body.consentAuth);
      });
  }

  private navigate(authorizationId: string, res: ConsentAuth) {
    this.sessionService.setConsentState(authorizationId, new AuthConsentState(res.violations));
    this.router.navigate([EntryPagePaymentsComponent.ROUTE], { relativeTo: this.activatedRoute.parent });
  }
}
