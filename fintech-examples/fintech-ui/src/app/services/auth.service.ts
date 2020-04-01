import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { HeaderConfig } from '../models/consts';
import { DocumentCookieService } from './document-cookie.service';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private router: Router,
    private finTechAuthorizationService: FinTechAuthorizationService,
    private cookieService: DocumentCookieService,
    private storageService: StorageService
  ) {}

  login(credentials: Credentials): Observable<boolean> {
    return this.finTechAuthorizationService.loginPOST('', credentials, 'response').pipe(
      map(response => {
        this.setSessionData(response, credentials);
        return response.ok;
      })
    );
  }

  logout(): void {
    if (!this.isLoggedIn()) {
      this.openLoginPage();
      return;
    }
      this.finTechAuthorizationService.logoutPOST('', '', 'response')
        .toPromise()
        .finally(() => {
          this.deleteSessionData();
          this.openLoginPage();
        });
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }

  public isLoggedIn(): boolean {
    const token = this.storageService.getXsrfToken();
    return token !== undefined && token !== null;
  }

  private setSessionData(response: any, credentials: Credentials): void {
    this.storageService.setXsrfToken(response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN));
    this.storageService.setUserName(credentials.username);
  }

  private deleteSessionData() {
    this.storageService.clearStorage();
  }
}
