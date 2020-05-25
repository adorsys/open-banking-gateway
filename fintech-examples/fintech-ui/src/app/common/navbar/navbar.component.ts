import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { StorageService } from '../../services/storage.service';
import { toLocaleString } from '../../models/consts';
import { RedirectTupelForMap } from '../../bank/redirect-page/redirect-struct';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  constructor(private authService: AuthService, private storageService: StorageService) {}

  ngOnInit() {}

  onLogout() {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserName(): string {
    return this.storageService.getUserName();
  }

  getSessionValidUntil(): string {
    return this.getSessionValidUntilAsString(this.storageService.getValidUntilDate());
  }

  getRedirectSessionsKeys(): string[] {
    return Array.from(this.storageService.getRedirectMap().keys());
  }

  getRedirectSessionValidUntil(redirectCode: string): string {
    return this.getSessionValidUntilAsString(this.storageService.getRedirectMap().get(redirectCode).validUntil);
  }
  private getSessionValidUntilAsString(validUntilDate: Date): string {
    if (validUntilDate !== null) {
      const validUntilDateString = toLocaleString(validUntilDate);
      const regEx = /.*([0-9]{2}:[0-9]{2}:[0-9]{2})/;
      const matches = validUntilDateString.match(regEx);
      if (matches.length !== 2) {
        throw new Error('valid until is not parsable ' + validUntilDateString);
      }
      return matches[1];
    }
    return '';
  }
}
