import {Injectable} from '@angular/core';
import {DocumentCookieService} from './document-cookie.service';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  constructor(private documentCookieService: DocumentCookieService) {
  }

  public getXsrfToken(): string {
    return localStorage.getItem(Session.XSRF_TOKEN);
  }

  public setXsrfToken(xsrfToken: string): void {
    // the xsrf token must contain the maxAge of the sessionCookie
    let regEx = /(.*);\smaxAge=(.*)/;
    let matches = xsrfToken.match(regEx);
    if (matches.length != 3) {
      throw "xsrfToken does not look like as expected:" + xsrfToken;
    }

    localStorage.setItem(Session.XSRF_TOKEN, matches[1]);
    this.setMaxAge(parseInt(matches[2]));
  }

  public getUserName(): string {
    return localStorage.getItem(Session.USERNAME);
  }

  public setUserName(userName: string): void {
    localStorage.setItem(Session.USERNAME, userName);
  }

  public getBankName(): string {
    return localStorage.getItem(Session.BANK_NAME);
  }

  public setBankName(bankName: string): void {
    localStorage.setItem(Session.BANK_NAME, bankName);
  }

  public getAuthId(): string {
    return localStorage.getItem(Session.AUTH_ID);
  }

  public setAuthId(authId: string): void {
    console.log("set authid to " + authId);
    localStorage.setItem(Session.AUTH_ID, authId);
  }

  public setMaxAge(maxAge: number): void {
    const timestamp = new Date().getTime() + maxAge * 1000;
    localStorage.setItem(Session.MAX_VALID_UNTIL, '' + timestamp);
    console.log("set max age " + maxAge + " till " + timestamp);
  }

  public isMaxAgeValid(): boolean {
    const validUntilTimestamp = localStorage.getItem(Session.MAX_VALID_UNTIL);
    if (validUntilTimestamp == undefined || validUntilTimestamp == null) {
      console.log("valid until unknown, so isMaxValid = false");
      return false;
    }
    const validUntil = parseInt(validUntilTimestamp);
    const timestamp = new Date().getTime();
    if (timestamp > validUntil) {
      console.log("valid until is " + validUntilTimestamp + " now is " + timestamp + ", so isMaxValid = false");
      return false;

    }
    return true;
  }

  public setRedirectActive(val: boolean) : void {
    localStorage.setItem(Session.REDIRECT_ACTIVE, val? "1" : "0");
  }

  public getRedirectActive(): boolean {
    const active = localStorage.getItem(Session.REDIRECT_ACTIVE);
    if (active == undefined || active == null) {
      return false;
    }
    return parseInt(active) == 1;
  }

  public clearStorage() {
    localStorage.clear();
    this.documentCookieService.delete(Session.COOKIE_NAME_SESSION);
  }
}

enum Session {
  USERNAME = 'USERNAME',
  BANK_NAME = 'BANK_NAME',
  XSRF_TOKEN = 'XSRF_TOKEN',
  COOKIE_NAME_SESSION = 'SESSION-COOKIE',
  AUTH_ID = 'AUTH_ID',
  MAX_VALID_UNTIL = "MAX_VALID_UNTIL_TIMESTAMP",
  REDIRECT_ACTIVE = "REDIRECT_ACTIVE"
}
