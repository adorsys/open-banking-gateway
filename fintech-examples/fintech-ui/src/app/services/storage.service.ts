import { Injectable } from '@angular/core';
import { DocumentCookieService } from './document-cookie.service';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  constructor(private documentCookieService: DocumentCookieService) {}

  public getXsrfToken(): string {
    return localStorage.getItem(Session.XSRF_TOKEN);
  }

  public setXsrfToken(xsrfToken: any): void {
    localStorage.setItem(Session.XSRF_TOKEN, xsrfToken);
  }

  public getUserName(): string {
    return localStorage.getItem(Session.USERNAME);
  }

  public setUserName(userName: string): void {
    localStorage.setItem(Session.USERNAME, userName);
  }

  public getBankName(): string {
    return localStorage.getItem(Session.BANKNAME);
  }

  public setBankName(bankName: string): void {
    localStorage.setItem(Session.BANKNAME, bankName);
  }

  public clearStorage() {
    localStorage.clear();
    this.documentCookieService.delete(Session.COOKIE_NAME_SESSION);
  }
}

enum Session {
  USERNAME = 'USERNAME',
  BANKNAME = 'BANKNAME',
  XSRF_TOKEN = 'XSRF_TOKEN',
  COOKIE_NAME_SESSION = 'SESSION-COOKIE'
}
