import { Injectable } from '@angular/core';

import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard  {
  constructor(private authService: AuthService) {}

  canActivate(): boolean {
    const isLoggedIn = this.authService.isLoggedIn();
    if (!isLoggedIn) {
      this.authService.logout();
    }
    return isLoggedIn;
  }
}
