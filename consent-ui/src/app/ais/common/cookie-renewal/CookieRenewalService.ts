import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {SimpleTimer} from 'ng2-simple-timer';
import {PsuAuthenticationService} from '../../../api-auth';
import * as uuid from 'uuid';
import {ApiHeaders} from "../../../api/api.headers";

@Injectable({
  providedIn: 'root'
})
export class CookieRenewalService implements OnInit, OnDestroy {

  public static TIMER_NAME = 'cookie-renewal-timer';
  public static ROUTE = 'login';

  constructor(private simpleTimer: SimpleTimer, private psuAuthService: PsuAuthenticationService) {
  }


  activate(authid): void {
    this.simpleTimer.newTimerCD(CookieRenewalService.TIMER_NAME, 10);
    this.simpleTimer.subscribe(CookieRenewalService.TIMER_NAME, () => this.cookieRenewal(authid));
  }

  ngOnInit(): void {
    console.log("CookieRenewalService is initialized");
  }

  ngOnDestroy(): void {
    console.log("CookieRenewalService is destroyed");
  }

  cookieRenewal(authid): void {
    console.log(new Date().toLocaleString() + ' renew timer');

    this.psuAuthService.renewalAuthorizationSessionKey('' + uuid.v4(), authid, 'response')
      .subscribe(res => {
        console.log("got response from server ", res.status);
        localStorage.setItem(ApiHeaders.COOKIE_TTL, res.headers.get(ApiHeaders.COOKIE_TTL));
        console.log(new Date().toLocaleString() + ' next time should be in  ' + localStorage.getItem(ApiHeaders.COOKIE_TTL));
    });
  }

}
