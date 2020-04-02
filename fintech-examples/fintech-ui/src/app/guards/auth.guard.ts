import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { DocumentCookieService } from '../services/document-cookie.service';
import { LocalStorage } from '../models/local-storage';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private cookieService: DocumentCookieService) {}

  canActivate(): boolean {
    const isLoggedIn = LocalStorage.isLoggedIn();

    if (!isLoggedIn) {
      this.authService.logout();
    }
    return isLoggedIn;
  }
}
