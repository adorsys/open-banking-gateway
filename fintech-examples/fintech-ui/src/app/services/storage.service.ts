import { Injectable } from '@angular/core';
import { toLocaleString } from '../models/consts';
import { RedirectTupelForMap } from '../bank/redirect-page/redirect-struct';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  public getXsrfToken(): string {
    return localStorage.getItem(Session.XSRF_TOKEN);
  }

  public setXsrfToken(xsrfToken: string, maxAge: number): void {
    localStorage.setItem(Session.XSRF_TOKEN, xsrfToken);
    this.setMaxAge(maxAge);
  }

  public setRedirect(redirectCode: string, authId: string, xsrfToken: string, maxAge: number): void {
    console.log('REDIRECT STARTED ', redirectCode);
    const tupel: RedirectTupelForMap = new RedirectTupelForMap();
    tupel.authId = authId;
    tupel.xsrfToken = xsrfToken;
    tupel.validUntil = new Date(new Date().getTime() + maxAge * 1000);
    const redirectMap = this.getRedirectMap();
    redirectMap.set(redirectCode, tupel);
    this.setRedirectMap(redirectMap);
  }

  resetRedirectCode(redirectCode: string) {
    console.log('REDIRECT FINISHED ', redirectCode);
    const redirectMap = this.getRedirectMap();
    redirectMap.delete(redirectCode);
    this.setRedirectMap(redirectMap);
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

  private setMaxAge(maxAge: number): void {
    const timestamp = new Date().getTime() + maxAge * 1000;
    localStorage.setItem(Session.MAX_VALID_UNTIL, '' + timestamp);
    console.log('set max age ' + maxAge + ' till ' + toLocaleString(new Date(timestamp)));
  }

  public getValidUntilDate(): Date {
    const validUntilTimestamp = localStorage.getItem(Session.MAX_VALID_UNTIL);
    if (validUntilTimestamp === undefined || validUntilTimestamp === null) {
      return null;
    }
    const date = new Date(parseInt(validUntilTimestamp, 0));
    if (toLocaleString(date) === 'Invalid Date') {
      console.log('programming error: ' + validUntilTimestamp + ' results in Invalid Date');
      throw Error('programming error: ' + validUntilTimestamp + ' results in Invalid Date');
    }
    return date;
  }

  public getRedirectMap(): Map<string, RedirectTupelForMap> {
    let mapString = localStorage.getItem(Session.REDIRECT_MAP);
    if (mapString == null) {
      this.setRedirectMap(new Map<string, RedirectTupelForMap>());
      mapString = localStorage.getItem(Session.REDIRECT_MAP);
    }
    return new Map(JSON.parse(mapString));
  }

  public isAnySessionValid(): boolean {
    const date: Date = this.getValidUntilDate();
    if (this.isDateValid(date)) {
      return true;
    }
    for (let redirectSession of Array.from(this.getRedirectMap().values())) {
      if (this.isDateValid(redirectSession.validUntil)) {
        return true;
      }
    }
    return false;
  }

  private isDateValid(validUntilDate: Date): boolean {
    if (validUntilDate === null) {
      return false;
    }
    const validUntil = validUntilDate.getTime();
    const timestamp = new Date().getTime();
    if (timestamp > validUntil) {
      console.log(
        'valid until was ' +
          toLocaleString(validUntilDate) +
          ' now is ' +
          toLocaleString(new Date()) +
          ', so isMaxValid = false'
      );
      return false;
    }
    return true;
  }

  public clearStorage() {
    localStorage.clear();
  }

  private setRedirectMap(map: Map<string, RedirectTupelForMap>) {
    localStorage.setItem(Session.REDIRECT_MAP, JSON.stringify(Array.from(map.entries())));
  }
}

enum Session {
  USERNAME = 'USERNAME',
  BANK_NAME = 'BANK_NAME',
  XSRF_TOKEN = 'XSRF_TOKEN',
  COOKIE_NAME_SESSION = 'SESSION-COOKIE',
  MAX_VALID_UNTIL = 'MAX_VALID_UNTIL_TIMESTAMP',
  REDIRECT_MAP = 'REDIRECT_MAP'
}
