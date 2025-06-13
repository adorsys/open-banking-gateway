import { Injectable } from '@angular/core';
import {
  ConsentSettingType,
  Consts,
  LoARetrievalInformation,
  LoTRetrievalInformation,
  toLocaleString
} from '../models/consts';
import {
  AccountStruct,
  RedirectStruct,
  RedirectTupelForMap,
  RedirectType
} from '../bank/redirect-page/redirect-struct';
import { SettingsData } from '../bank/settings/settings.component';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  static isoDate(toConvert: Date) {
    return toConvert.toISOString().split('T')[0];
  }

  public getXsrfToken(): string {
    return localStorage.getItem(Session.XSRF_TOKEN);
  }

  public setXsrfToken(xsrfToken: string, maxAge: number): void {
    localStorage.setItem(Session.XSRF_TOKEN, xsrfToken);
    localStorage.setItem(Session.MAX_VALID_UNTIL, JSON.stringify(this.getMaxAgeDate(maxAge)));
  }

  public extendSessionAge(maxAge: number): void {
    localStorage.setItem(Session.MAX_VALID_UNTIL, JSON.stringify(this.getMaxAgeDate(maxAge)));
  }

  public setRedirect(
    redirectCode: string,
    authId: string,
    xsrfToken: string,
    maxAge: number,
    redirectType: RedirectType
  ): void {
    console.log('REDIRECT STARTED ', redirectCode);
    const tupel: RedirectTupelForMap = new RedirectTupelForMap();
    tupel.authId = authId;
    tupel.xsrfToken = xsrfToken;
    tupel.validUntil = new Date(new Date().getTime() + maxAge * 1000);
    tupel.redirectType = redirectType;
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

  private getMaxAgeDate(maxAge: number): Date {
    const date = new Date(new Date().getTime() + maxAge * 1000);
    return date;
  }

  public getValidUntilDate(): Date {
    const validUntilTimestamp = localStorage.getItem(Session.MAX_VALID_UNTIL);
    if (validUntilTimestamp === undefined || validUntilTimestamp === null) {
      return null;
    }
    const date: Date = new Date(JSON.parse(validUntilTimestamp));
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

  public isLoggedIn(): boolean {
    return this.isAnySessionValid();
  }

  public setLoa(bankId: string, accountStruct: AccountStruct[]): void {
    localStorage.setItem(bankId, JSON.stringify(accountStruct));
  }

  public getLoa(bankId: string): AccountStruct[] {
    const value = localStorage.getItem(bankId);
    if (value === null) {
      return null;
    }
    return JSON.parse(value);
  }

  public getUserRedirected(): boolean {
    const value = localStorage.getItem(Session.USER_REDIRECTED);
    if (value === null) {
      return false;
    }
    return JSON.parse(value);
  }

  public setUserRedirected(redirected: boolean): void {
    localStorage.setItem(Session.USER_REDIRECTED, JSON.stringify(redirected));
  }

  private isAnySessionValid(): boolean {
    const date: Date = this.getValidUntilDate();
    if (this.isDateValid(date)) {
      return true;
    }
    for (const redirectSession of Array.from(this.getRedirectMap().values())) {
      if (this.isDateValid(new Date(redirectSession.validUntil))) {
        return true;
      }
    }
    return false;
  }

  private isDateValid(validUntilDate: Date): boolean {
    if (validUntilDate === null) {
      return false;
    }
    const validUntil: number = validUntilDate.getTime();
    const timestamp: number = new Date().getTime();
    if (timestamp > validUntil) {
      // console.log('valid until was ' + toLocaleString(validUntilDate) + ' now is ' + toLocaleString(new Date())
      // + ', so isMaxValid = false');
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

  public getSettings(): SettingsData {
    const setting = localStorage.getItem(Consts.LOCAL_STORAGE_SETTINGS);

    if (setting == null) {
      return {
        loa: LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT,
        lot: LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT,
        withBalance: true,
        cacheLoa: true,
        cacheLot: true,
        consentRequiresAuthentication: true,
        paymentRequiresAuthentication: false,
        consentSettingType: ConsentSettingType.NONE,
        consent: null,
        dateFrom: '1970-01-01',
        dateTo: StorageService.isoDate(new Date())
      };
    }
    return JSON.parse(setting);
  }

  public setSettings(data: SettingsData) {
    localStorage.setItem(Consts.LOCAL_STORAGE_SETTINGS, JSON.stringify(data));
  }

  public deleteSettings(): void {
    localStorage.removeItem(Consts.LOCAL_STORAGE_SETTINGS);
  }

  public setAfterRedirect(afterRedirect: boolean) {
    localStorage.setItem(Session.AFTER_REDIRECT, JSON.stringify(afterRedirect));
  }

  public isAfterRedirect() {
    const value = localStorage.getItem(Session.AFTER_REDIRECT);
    if (value === null) {
      return false;
    }
    return JSON.parse(value);
  }

  public createRedirectStruct(redirectUrl: string, redirectCode: string, bankId: string): RedirectStruct {
    const r = new RedirectStruct();
    r.redirectUrl = encodeURIComponent(redirectUrl);
    r.redirectCode = redirectCode;
    r.bankId = bankId;
    r.bankName = this.getBankName();
    return r;
  }
}

enum Session {
  USERNAME = 'USERNAME',
  BANK_NAME = 'BANK_NAME',
  XSRF_TOKEN = 'XSRF_TOKEN',
  MAX_VALID_UNTIL = 'MAX_VALID_UNTIL_TIMESTAMP',
  REDIRECT_MAP = 'REDIRECT_MAP',
  LOA = 'LOA',
  USER_REDIRECTED = 'USER_REDIRECTED',
  REDIRECT_CANCEL_URL = 'REDIRECT_CANCEL_URL',
  AFTER_REDIRECT = 'AFTER_REDIRECT'
}
