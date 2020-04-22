import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';
import { EntryPageTransactionsComponent } from '../transactions/entry-page-transactions/entry-page-transactions.component';
import { EntryPageAccountsComponent } from '../accounts/entry-page-accounts/entry-page-accounts.component';
import { SessionService } from '../../../../common/session.service';
import { ApiHeaders } from '../../../../api/api.headers';
import { AuthConsentState } from '../../../common/dto/auth-state';
import { ConsentAuth, ConsentAuthorizationService } from '../../../../api';
import ActionEnum = ConsentAuth.ActionEnum;

@Component({
  selector: 'consent-app-consent-initiate',
  templateUrl: './consent-initiate.component.html',
  styleUrls: ['./consent-initiate.component.scss']
})
export class ConsentInitiateComponent implements OnInit {
  private redirectCode: string;
  private route: ActivatedRouteSnapshot;
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sessionService: SessionService,
    private consentAuthService: ConsentAuthorizationService
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

    if (ConsentInitiateComponent.isInvalid(authId, this.redirectCode)) {
      this.abortUnauthorized();
    } else {
      this.initiateConsentSession(authId, this.redirectCode);
    }
  }

  private abortUnauthorized() {
    this.router.navigate(['./error'], { relativeTo: this.activatedRoute.parent });
  }

  private initiateConsentSession(authorizationId: string, redirectCode: string) {
    this.consentAuthService.authUsingGET(authorizationId, redirectCode, 'response').subscribe(
      res => {
        this.sessionService.setRedirectCode(authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));

        this.navigate(authorizationId, res.body.consentAuth);
      },
      error => {
        console.log(error);
      }
    );
  }

  private navigate(authorizationId: string, res: ConsentAuth) {
    switch (res.action) {
      case ActionEnum.ACCOUNTS:
        this.sessionService.setConsentState(authorizationId, new AuthConsentState(res.violations));
        this.router.navigate([EntryPageAccountsComponent.ROUTE], { relativeTo: this.activatedRoute.parent });
        break;
      case ActionEnum.TRANSACTIONS:
        this.sessionService.setConsentState(authorizationId, new AuthConsentState(res.violations));
        this.router.navigate([EntryPageTransactionsComponent.ROUTE], { relativeTo: this.activatedRoute.parent });
        break;
      default:
        console.log(res);
        throw new Error('Cannot handle action: ' + res.action);
    }
  }
}
