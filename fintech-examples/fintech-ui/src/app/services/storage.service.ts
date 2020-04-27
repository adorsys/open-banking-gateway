import {Injectable} from '@angular/core';
import {DocumentCookieService} from './document-cookie.service';
import {toLocaleString} from '../models/consts';

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
    const regEx = /(.*);\sMax-Age=(.*)/;
    const matches = xsrfToken.match(regEx);
    if (matches.length !== 3) {
      throw new Error('xsrfToken does not look like as expected:' + xsrfToken);
    }

    localStorage.setItem(Session.XSRF_TOKEN, matches[1]);
    this.setMaxAge(parseInt(matches[2], 10));
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
    console.log('set authid to ' + authId);
    localStorage.setItem(Session.AUTH_ID, authId);
  }

  public setMaxAge(maxAge: number): void {
    const timestamp = new Date().getTime() + maxAge * 1000;
    localStorage.setItem(Session.MAX_VALID_UNTIL, '' + timestamp);
    console.log('set max age ' + maxAge + ' till ' + toLocaleString(new Date(timestamp)));
  }

  public setRedirectCode(redirectCode: string): void {
    localStorage.setItem(Session.REDIRECT_CODE, redirectCode);
  }

  public getRedirectCode(): string {
    return localStorage.getItem(Session.REDIRECT_CODE);
  }

  public getValidUntilDate(): Date {
    const validUntilTimestamp = localStorage.getItem(Session.MAX_VALID_UNTIL);
    if (validUntilTimestamp === undefined || validUntilTimestamp === null) {
      return null;
    }
    const date = new Date(parseInt(validUntilTimestamp, 0));
    if (toLocaleString(date) === 'Invalid Date') {
      console.log('HELLO VALENTYN, THIS IS WEIRED: ' + validUntilTimestamp + ' results in Invalid Date');

      return null;
    }
    return date;
  }

  public isMaxAgeValid(): boolean {
    const validUntilDate: Date = this.getValidUntilDate();
    if (validUntilDate === null) {
//      console.log('valid until unknown, so isMaxValid = false');
      return false;
    }
    const validUntil = validUntilDate.getTime();
    const timestamp = new Date().getTime();
    if (timestamp > validUntil) {
      console.log('valid until was ' + toLocaleString(validUntilDate) + ' now is '
        + toLocaleString(new Date()) + ', so isMaxValid = false');
      return false;
    }
    // console.log('valid until was ' + tLocaleString(validUntilDate) + ' now is '
    // + tLocaleString(new Date()) + ', so isMaxValid = true');

    //    console.log('valid until was ' + validUntilDate.toLocaleString() +
    //    ' now is ' + new Date().toLocaleString() + ', so isMaxValid = true');
    return true;
  }

  public setRedirectActive(val: boolean): void {
    localStorage.setItem(Session.REDIRECT_ACTIVE, val ? '1' : '0');
  }

  public getRedirectActive(): boolean {
    const active = localStorage.getItem(Session.REDIRECT_ACTIVE);
    if (active === undefined || active === null) {
      return false;
    }
    return parseInt(active, 0) === 1;
  }

  public clearStorage() {
    localStorage.clear();
    this.documentCookieService.delete(Session.COOKIE_NAME_SESSION);
    let retries = 100;
    while (retries > 0 && this.documentCookieService.exists(Session.COOKIE_NAME_SESSION)) {
      console.log('retry to delete');
      this.documentCookieService.delete(Session.COOKIE_NAME_SESSION);
      retries--;
    }
  }
}

enum Session {
  USERNAME = 'USERNAME',
  BANK_NAME = 'BANK_NAME',
  XSRF_TOKEN = 'XSRF_TOKEN',
  COOKIE_NAME_SESSION = 'SESSION-COOKIE',
  AUTH_ID = 'AUTH_ID',
  MAX_VALID_UNTIL = 'MAX_VALID_UNTIL_TIMESTAMP',
  REDIRECT_ACTIVE = 'REDIRECT_ACTIVE',
  REDIRECT_CODE = 'REDIRECT_CODE'
}
