import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { HeaderConfig } from '../models/consts';
import { DocumentCookieService } from './document-cookie.service';
import { StorageService } from './storage.service';
import { RedirectTupelForMap } from '../bank/redirect-page/redirect-struct';

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
    this.storageService.clearStorage();
    return this.finTechAuthorizationService.loginPOST('', credentials, 'response').pipe(
      map(response => {
        this.setSessionData(response, credentials);
        return response.ok;
      })
    );
  }

  logout(): void {
    if (!this.storageService.isLoggedIn()) {
      this.openLoginPage();
      return;
    }
    this.finTechAuthorizationService
      .logoutPOST('', '', 'response')
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
    return this.storageService.isLoggedIn();
  }

  public getUserName(): string {
    return this.storageService.getUserName();
  }

  public getValidUntilDate(): Date {
    return this.storageService.getValidUntilDate();
  }

  public getRedirectMap(): Map<string, RedirectTupelForMap> {
    return this.storageService.getRedirectMap();
  }

  public extendSessionAge(maxAge: number): void {
    this.storageService.extendSessionAge(maxAge);
  }

  public getXsrfToken(): string {
    return this.storageService.getXsrfToken();
  }

  private setSessionData(response: any, credentials: Credentials): void {
    this.storageService.setXsrfToken(
      response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
      response.headers.get(HeaderConfig.HEADER_FIELD_X_MAX_AGE)
    );
    this.storageService.setUserName(credentials.username);
  }

  private deleteSessionData() {
    this.storageService.clearStorage();
  }
}
