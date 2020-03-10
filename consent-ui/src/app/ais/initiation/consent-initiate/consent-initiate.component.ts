import { Component, OnInit } from '@angular/core';
import {combineLatest} from "rxjs";
import {map} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";
import {SessionService} from "../../../common/session.service";
import {ConsentAuthorizationService} from "../../../api/consentAuthorization.service";
import {ApiHeaders} from "../../../api/api.headers";
import {AuthConsentState, AuthViolation} from "../../common/dto/auth-state";
import {EntryPageAccountsComponent} from "../accounts/entry-page-accounts/entry-page-accounts.component";
import {EntryPageTransactionsComponent} from "../transactions/entry-page-transactions/entry-page-transactions.component";

@Component({
  selector: 'consent-app-consent-initiate',
  templateUrl: './consent-initiate.component.html',
  styleUrls: ['./consent-initiate.component.scss']
})
export class ConsentInitiateComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private router: Router, private sessionService: SessionService,
              private consentAuthService: ConsentAuthorizationService) { }

  ngOnInit() {
    combineLatest(this.activatedRoute.params, this.activatedRoute.queryParams)
      .pipe(map(it => (new AuthorizationKey(it[0].authId, it[1].redirectCode))))
      .subscribe(it => {
        if (it.isInvalid()) {
          this.abortUnauthorized();
        } else {
          this.initiateConsentSession(it.authorizationId, it.redirectCode);
        }
      });
  }

  private abortUnauthorized() {
    this.router.navigate(['./error'], { relativeTo: this.activatedRoute.parent});
  }

  private initiateConsentSession(authorizationId: string, redirectCode: string) {
    this.consentAuthService.authUsingGET(authorizationId, redirectCode, 'response')
       .subscribe(res => {
         this.sessionService.setRedirectCode(authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
         this.navigate(authorizationId, res.body as AuthStateResponse);
       });
  }

  private navigate(authorizationId: string, res: AuthStateResponse) {
    switch (res.action) {
      case ServicedAction.LIST_ACCOUNTS:
        this.sessionService.setConsentState(authorizationId, new AuthConsentState(res.causes));
        this.router.navigate([EntryPageAccountsComponent.ROUTE], { relativeTo: this.activatedRoute.parent});
        break;
      case ServicedAction.LIST_TRANSACTIONS:
        this.sessionService.setConsentState(authorizationId, new AuthConsentState(res.causes));
        this.router.navigate([EntryPageTransactionsComponent.ROUTE], { relativeTo: this.activatedRoute.parent});
        break;
      default:
        console.log(res);
        throw new Error("Can't handle action: " + res.action);
    }
  }
}


class AuthorizationKey {
  constructor(public authorizationId: string, public redirectCode: string) {
  }

  isInvalid(): boolean {
    return !this.redirectCode || !this.authorizationId || '' === this.redirectCode || '' === this.authorizationId;
  }
}

export enum ServicedAction {
  LIST_ACCOUNTS = 'LIST_ACCOUNTS',
  LIST_TRANSACTIONS = 'LIST_TRANSACTIONS'
}

interface AuthStateResponse {
  action: ServicedAction;
  causes: AuthViolation[];
}
