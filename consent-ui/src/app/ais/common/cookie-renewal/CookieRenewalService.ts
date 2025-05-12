import { Injectable } from '@angular/core';
import { SimpleTimer } from 'src/app/utilities/simple-timer';
import { PsuAuthenticationService } from '../../../api-auth';
import * as uuid from 'uuid';
import { ApiHeaders } from '../../../api/api.headers';
import { SessionService } from '../../../common/session.service';

@Injectable({
  providedIn: 'root'
})
export class CookieRenewalService {
  public static TIMER_NAME = 'cookie-renewal-timer';
  public static ROUTE = 'login';

  constructor(
    private simpleTimer: SimpleTimer,
    private psuAuthService: PsuAuthenticationService,
    private sessionService: SessionService
  ) {}

  activate(authid): void {
    const timer = this.getTimer(authid);
    console.log(new Date().toLocaleString() + ' activate timer in ' + timer);
    this.simpleTimer.newTimerCD(this.getTimerName(authid), timer, timer);
    this.simpleTimer.subscribe(this.getTimerName(authid), () => this.cookieRenewal(authid));
  }

  cookieRenewal(authid): void {
    console.log(new Date().toLocaleString() + ' timer rings, request for cookie renewal');

    // delete old timer
    this.simpleTimer.unsubscribe(this.getTimerName(authid));
    this.simpleTimer.delTimer(this.getTimerName(authid));

    if (!this.sessionService.isLongTimeCookie(authid)) {
      // timer is deleted. If following call fails due to whatever reason, session cookie is not valid but
      // timer does not retry to renew it, which is fine, so error handling of call is not needed
      this.psuAuthService.renewalAuthorizationSessionKey('' + uuid.v4(), authid, 'response').subscribe((res) => {
        this.sessionService.setTTL(authid, res.headers.get(ApiHeaders.COOKIE_TTL));
        const timer = this.getTimer(authid);
        console.log(
          new Date().toLocaleString(),
          ' got new cookie from server ',
          res.status,
          ' and activate next timer ',
          this.getTimerName(authid),
          ' in ',
          timer,
          ' secs'
        );
        this.simpleTimer.newTimerCD(this.getTimerName(authid), timer, timer);
        this.simpleTimer.subscribe(this.getTimerName(authid), () => this.cookieRenewal(authid));
      });
    }
  }

  getTimer(authid): number {
    const ttl = parseInt(this.sessionService.getTTL(authid), 0);
    // backend minimum for ttl is 60 secs see FacadeTransientDataConfig.MIN_EXPIRE_SECONDS = 60L;
    return isNaN(ttl) ? 58 : ttl - 2;
  }

  getTimerName(authid: string): string {
    return CookieRenewalService.TIMER_NAME + authid;
  }
}
