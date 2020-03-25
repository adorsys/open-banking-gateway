import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { DocumentCookieService } from '../services/document-cookie.service';
import { Consts } from '../common/consts';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private cookieService: DocumentCookieService) {}

  canActivate(): boolean {
    const isLoggedIn = this.cookieService.exists(Consts.COOKIE_NAME_XSRF_TOKEN);

    if (!isLoggedIn) {
      this.authService.logout();
    }
    return isLoggedIn;
  }
}
