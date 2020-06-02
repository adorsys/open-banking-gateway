import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService) {}

  canActivate(): boolean {
    const logged =  this.authService.isLoggedIn();
    if (! logged) {
      this.authService.logout();
    }
    return logged;
  }
}
