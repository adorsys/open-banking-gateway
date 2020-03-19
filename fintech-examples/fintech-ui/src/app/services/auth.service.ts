import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { Consts } from '../common/consts';
import * as uuid from 'uuid';
import { DocumentCookieService } from './document-cookie.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private router: Router,
    private finTechAuthorizationService: FinTechAuthorizationService,
    private cookieService: DocumentCookieService
  ) {}

  login(credentials: Credentials): Observable<boolean> {
    return this.finTechAuthorizationService.loginPOST(uuid.v4(), credentials, 'response').pipe(
      map(response => {
        this.cookieService.getAll().forEach(cookie => console.log('cookie after login :' + cookie));
        localStorage.setItem(Consts.USERNAME, credentials.username);
        return response.ok;
      })
    );
  }

  logout(): void {
    localStorage.clear();
    this.cookieService.delete(Consts.XSRF_TOKEN);
    this.cookieService.delete(Consts.SESSION_COOKIE);
    this.openLoginPage();
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.cookieService.exists(Consts.XSRF_TOKEN);
  }

  getX_XSRF_TOKEN(): string {
    return this.cookieService.find(Consts.XSRF_TOKEN);
  }
}
