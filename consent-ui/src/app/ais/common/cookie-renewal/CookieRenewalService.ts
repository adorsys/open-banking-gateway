import { Injectable } from '@angular/core';
import { SimpleTimer } from 'ng2-simple-timer';
import { PsuAuthenticationService } from '../../../api-auth';
import * as uuid from 'uuid';
import { ApiHeaders } from '../../../api/api.headers';

@Injectable({
  providedIn: 'root'
})
export class CookieRenewalService {
  public static TIMER_NAME = 'cookie-renewal-timer';
  public static ROUTE = 'login';

  constructor(private simpleTimer: SimpleTimer, private psuAuthService: PsuAuthenticationService) {}

  activate(authid): void {
    const timer = this.getTimer();
    console.log(new Date().toLocaleString() + ' activate timer in ' + timer);
    this.simpleTimer.newTimerCD(CookieRenewalService.TIMER_NAME, timer, timer);
    this.simpleTimer.subscribe(CookieRenewalService.TIMER_NAME, () => this.cookieRenewal(authid));
  }

  cookieRenewal(authid): void {
    console.log(new Date().toLocaleString() + ' timer rings, request for cookie renewal');

    // delete old timer
    this.simpleTimer.unsubscribe(CookieRenewalService.TIMER_NAME);
    this.simpleTimer.delTimer(CookieRenewalService.TIMER_NAME);

    // timer is deleted. If following call fails due to whatever reason, session cookie is not valid but
    // timer does not retry to renew it, which is fine, so error handling of call is not needed
    this.psuAuthService.renewalAuthorizationSessionKey('' + uuid.v4(), authid, 'response').subscribe(res => {
      console.log(new Date().toLocaleString() + ' got new cookie from server ', res.status);
      localStorage.setItem(ApiHeaders.COOKIE_TTL, res.headers.get(ApiHeaders.COOKIE_TTL));
      const timer = this.getTimer();

      console.log(new Date().toLocaleString() + ' activate next timer in ' + timer);
      this.simpleTimer.newTimerCD(CookieRenewalService.TIMER_NAME, timer, timer);
      this.simpleTimer.subscribe(CookieRenewalService.TIMER_NAME, () => this.cookieRenewal(authid));
    });
  }

  getTimer(): number {
    const ttl = parseInt(localStorage.getItem(ApiHeaders.COOKIE_TTL), 0);
    // backend minimum for ttl is 60 secs
    return isNaN(ttl) ? 58 : ttl - 2;
  }
}
