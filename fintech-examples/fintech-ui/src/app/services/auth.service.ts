import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { Consts } from '../common/consts';
import * as uuid from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private router: Router,
    private cookieService: CookieService,
    private finTechAuthorizationService: FinTechAuthorizationService
  ) {}

  login(credentials: Credentials): Observable<boolean> {
    return this.finTechAuthorizationService.loginPOST(uuid.v4(), credentials, 'response').pipe(
      map(loginResponse => {
        // if login response is ok and cookie exist then the login was successful
        localStorage.setItem(Consts.USERNAME, credentials.username);
        console.log('after login get all cookies is ', JSON.stringify(this.cookieService.getAll()));
        return loginResponse.ok && this.cookieService.check(Consts.XSRF_TOKEN);
      })
    );
  }

  logout(): void {
    localStorage.clear();
    console.log('before logout get all cookies is ', JSON.stringify(this.cookieService.getAll()));

    this.cookieService.delete(Consts.XSRF_TOKEN);
    const xsrftoken = this.cookieService.get(Consts.XSRF_TOKEN);
    if (xsrftoken !== undefined) {
      console.error('logut did not work, xsrf-token-cookie still exists');
    }
    this.openLoginPage();
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.cookieService.check(Consts.XSRF_TOKEN);
  }

  getX_XSRF_TOKEN(): string {
    return this.cookieService.get(Consts.XSRF_TOKEN);
  }
}
