import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { StorageService } from '../services/storage.service';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private storageService: StorageService) {}

  canActivate(): boolean {
    const logged =  this.storageService.isLoggedIn();
    if (! logged) {
      this.authService.logout();
    }
    return logged;
  }
}
