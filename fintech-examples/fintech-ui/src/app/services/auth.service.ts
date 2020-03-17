import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { Consts } from '../common/consts';
import * as uuid from 'uuid';
import { timer } from 'rxjs/internal/observable/timer';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private XSRF_TOKEN = 'XSRF-TOKEN';
  time = timer(0);

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
        return loginResponse.ok && this.cookieService.check(this.XSRF_TOKEN);
      })
    );
  }

  logout(): void {
    this.cookieService.deleteAll('/');
    this.cookieService.set('SESSION-COOKIE', null);
    this.cookieService.delete('XSRF-TOKEN', null);
    console.log('cookies values XSRF-TOKEN', this.cookieService.get('XSRF-TOKEN'));
    this.openLoginPage();
    localStorage.clear();
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.cookieService.check(this.XSRF_TOKEN);
  }

  getX_XSRF_TOKEN(): string {
    return this.cookieService.get(this.XSRF_TOKEN);
  }
}
