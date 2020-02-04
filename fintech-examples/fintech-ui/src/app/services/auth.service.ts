import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private XSRF_TOKEN = 'XSRF-TOKEN';

  constructor(
    private router: Router,
    private cookieService: CookieService,
    private finTechAuthorizationService: FinTechAuthorizationService
  ) {}

  login(credentials: Credentials): Observable<boolean> {
    return this.finTechAuthorizationService
      .loginPOST('99391c7e-ad88-49ec-a2ad-99ddcb1f7721', credentials, 'response')
      .pipe(
        map(loginResponse => {
          // if login response is ok and cookie exist then the login was successful
          return loginResponse.ok && this.cookieService.check(this.XSRF_TOKEN);
        })
      );
  }

  logout(): void {
    this.cookieService.deleteAll();
    this.openLoginPage();
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
