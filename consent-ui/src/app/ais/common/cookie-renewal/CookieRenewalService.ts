import {Injectable} from '@angular/core';
import {SimpleTimer} from 'ng2-simple-timer';
import {PsuAuthenticationService} from '../../../api-auth';
import * as uuid from 'uuid';
import {ApiHeaders} from '../../../api/api.headers';

@Injectable({
  providedIn: 'root'
})
export class CookieRenewalService {

  public static TIMER_NAME = 'cookie-renewal-timer';
  public static ROUTE = 'login';

  constructor(private simpleTimer: SimpleTimer, private psuAuthService: PsuAuthenticationService) {
  }


  activate(authid): void {
    this.simpleTimer.newTimerCD(CookieRenewalService.TIMER_NAME, 1, 1);
    this.simpleTimer.subscribe(CookieRenewalService.TIMER_NAME, () => this.cookieRenewal(authid));
  }

  cookieRenewal(authid): void {

    const ttl = this.getTTL();
    if (ttl === 0) {
      return;
    }

    console.log(new Date().toLocaleString() + ' delete old timer');
    this.simpleTimer.unsubscribe(CookieRenewalService.TIMER_NAME);
    this.simpleTimer.delTimer(CookieRenewalService.TIMER_NAME);

    // timer is deleted. If following call fails due to whatever reason, session cookie is not valid but
    // timer does not retry to renew it, which is fine, so error handling of call is not needed
    this.psuAuthService.renewalAuthorizationSessionKey('' + uuid.v4(), authid, 'response')
      .subscribe(res => {
        console.log('got new cookie from server ', res.status);
        localStorage.setItem(ApiHeaders.COOKIE_TTL, res.headers.get(ApiHeaders.COOKIE_TTL));
        console.log(new Date().toLocaleString() + ' next time should be in  ' + localStorage.getItem(ApiHeaders.COOKIE_TTL));

        console.log(new Date().toLocaleString() + ' activate new timer');
        this.simpleTimer.newTimerCD(CookieRenewalService.TIMER_NAME, ttl-2, ttl-2);
        this.simpleTimer.subscribe(CookieRenewalService.TIMER_NAME, () => this.cookieRenewal(authid));

      });
  }

  getTTL() : number {
    const ttlstring = localStorage.getItem(ApiHeaders.COOKIE_TTL);
    if (ttlstring === null || ttlstring === '0') {
      console.log(new Date().toLocaleString() + ' timer is idle');
      return 0;
    }

    return parseInt(ttlstring,0);
  }

}
