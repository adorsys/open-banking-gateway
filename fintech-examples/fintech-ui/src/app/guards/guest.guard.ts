import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { DocumentCookieService } from '../services/document-cookie.service';
import { Consts } from '../common/consts';

@Injectable({
  providedIn: 'root'
})
export class GuestGuard implements CanActivate {
  constructor(private cookieService: DocumentCookieService, private router: Router) {}

  canActivate(): boolean {
    const isLoggedIn = this.cookieService.exists(Consts.COOKIE_NAME_XSRF_TOKEN);

    if (isLoggedIn) {
      this.router.navigate(['/search']);
    }
    return true;
  }
}
