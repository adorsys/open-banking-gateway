import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { DocumentCookieService } from '../services/document-cookie.service';
import {LocalStorage} from "../common/local-storage";

@Injectable({
  providedIn: 'root'
})
export class GuestGuard implements CanActivate {
  constructor(private cookieService: DocumentCookieService, private router: Router) {}

  canActivate(): boolean {
    const isLoggedIn = LocalStorage.isLoggedIn();

    if (isLoggedIn) {
      this.router.navigate(['/search']);
    }
    return true;
  }
}
