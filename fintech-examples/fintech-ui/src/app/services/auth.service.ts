import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { Consts } from '../common/consts';
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
    return this.finTechAuthorizationService.loginPOST('', credentials, 'response').pipe(
      map(response => {
        this.cookieService.getAll().forEach(cookie => console.log('cookie after login :' + cookie));
        localStorage.setItem(Consts.LOCAL_STORAGE_USERNAME, credentials.username);
        return response.ok;
      })
    );
  }

  logout() {
    console.log('start logout');
    this.cookieService.delete(Consts.COOKIE_NAME_XSRF_TOKEN);
    this.cookieService.delete(Consts.COOKIE_NAME_SESSION_COOKIE);
    this.cookieService.getAll().forEach(cookie => console.log('cookie after logout :' + cookie));
    this.finTechAuthorizationService.logoutPOST('', '', 'response').subscribe(response => {
      if (response.ok) {
        console.log('logout confirmed by server');
        localStorage.clear();
      } else {
        console.error('log off not possible due to server response:' + response.status);
      }
    });
    this.openLoginPage();
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }
}
