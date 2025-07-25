import { Injectable } from '@angular/core';
import { ConsentAuth } from '../api';
import { AuthConsentState } from '../ais/common/dto/auth-state';
import { PisPayment } from '../pis/common/models/pis-payment.model';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  constructor() {}

  public setTTL(authorizationId: string, ttl: string) {
    sessionStorage.setItem(authorizationId + Session.COOKIE_TTL, ttl);
  }

  public isLongTimeCookie(authorizationId: string) {
    return sessionStorage.getItem(authorizationId + Session.IS_LONG_LIFE_COOKIE);
  }

  public setIsLongTimeCookie(authorizationId: string, value: boolean) {
    sessionStorage.setItem(authorizationId + Session.IS_LONG_LIFE_COOKIE, value.toString());
  }

  public getTTL(authorizationId: string): string {
    return sessionStorage.getItem(authorizationId + Session.COOKIE_TTL);
  }

  public getRedirectCode(authorizationId: string): string {
    return sessionStorage.getItem(authorizationId + Session.REDIRECT_CODE);
  }
  public setRedirectCode(authorizationId: string, redirectCode: string) {
    sessionStorage.setItem(authorizationId + Session.REDIRECT_CODE, redirectCode);
  }

  public getFintechName(authorizationId: string): string {
    return sessionStorage.getItem(authorizationId + Session.FINTECH_NAME);
  }

  public setFintechName(authorizationId: string, fintechName: string) {
    sessionStorage.setItem(authorizationId + Session.FINTECH_NAME, fintechName);
  }

  public getConsentTypesSupported(authorizationId: string): ConsentAuth.SupportedConsentTypesEnum[] {
    const supportedTypes = sessionStorage.getItem(authorizationId + Session.CONSENT_TYPES_SUPPORTED);
    if (!supportedTypes) {
      return null;
    }
    return JSON.parse(supportedTypes);
  }

  public setConsentTypesSupported(authorizationId: string, consentTypes: ConsentAuth.SupportedConsentTypesEnum[]) {
    sessionStorage.setItem(
      authorizationId + Session.CONSENT_TYPES_SUPPORTED,
      !consentTypes ? null : JSON.stringify(consentTypes)
    );
  }

  public getBankName(authorizationId: string): string {
    return sessionStorage.getItem(authorizationId + Session.BANK_NAME);
  }

  public setBankName(authorizationId: string, bankName: string) {
    sessionStorage.setItem(authorizationId + Session.BANK_NAME, bankName);
  }

  public setConsentState(authorizationId: string, consentState: AuthConsentState) {
    sessionStorage.setItem(authorizationId + Session.CONSENT_STATE, JSON.stringify(consentState));
  }

  public getConsentState<T>(authorizationId: string, factory: () => T): T {
    if (!sessionStorage.getItem(authorizationId + Session.CONSENT_STATE)) {
      return null;
    }

    return Object.assign(factory(), JSON.parse(sessionStorage.getItem(authorizationId + Session.CONSENT_STATE)));
  }

  public setConsentObject<T>(authorizationId: string, consentObject: T) {
    sessionStorage.setItem(authorizationId + Session.CONSENT_OBJECT, JSON.stringify(consentObject));
  }

  public getConsentObject<T>(authorizationId: string, factory: () => T): T {
    if (!sessionStorage.getItem(authorizationId + Session.CONSENT_OBJECT)) {
      return null;
    }

    return Object.assign(factory(), JSON.parse(sessionStorage.getItem(authorizationId + Session.CONSENT_OBJECT)));
  }

  public setPaymentState(authorizationId: string, paymentState: AuthConsentState) {
    sessionStorage.setItem(authorizationId + Session.PAYMENT_STATE, JSON.stringify(paymentState));
  }

  public getPaymentState<T>(authorizationId: string, factory: () => T): T {
    if (!sessionStorage.getItem(authorizationId + Session.PAYMENT_STATE)) {
      return null;
    }

    return Object.assign(factory(), JSON.parse(sessionStorage.getItem(authorizationId + Session.PAYMENT_STATE)));
  }

  public setPaymentObject(authorizationId: string, paymentObject: PisPayment) {
    sessionStorage.setItem(authorizationId + Session.PAYMENT_OBJECT, JSON.stringify(paymentObject));
  }

  public getPaymentObject<T>(authorizationId: string): T {
    if (!sessionStorage.getItem(authorizationId + Session.PAYMENT_OBJECT)) {
      return null;
    }

    return Object.assign(JSON.parse(sessionStorage.getItem(authorizationId + Session.PAYMENT_OBJECT)));
  }

  public setXsrfToken(token: string) {
    sessionStorage.setItem(Session.XSRF_TOKEN, token);
  }
  public getXsrfToken() {
    sessionStorage.getItem(Session.XSRF_TOKEN);
  }
}

enum Session {
  REDIRECT_CODE = ':REDIRECT_CODE',
  CONSENT_STATE = ':CONSENT_STATE',
  CONSENT_OBJECT = ':CONSENT_OBJECT',
  PAYMENT_OBJECT = ':PAYMENT_OBJECT',
  PAYMENT_STATE = ':PAYMENT_STATE',
  FINTECH_NAME = ':FINTECH_NAME',
  CONSENT_TYPES_SUPPORTED = ':CONSENT_TYPES_SUPPORTED',
  BANK_NAME = ':BANK_NAME',
  XSRF_TOKEN = 'XSRF_TOKEN',
  COOKIE_TTL = 'Cookie-TTL',
  IS_LONG_LIFE_COOKIE = ':IS-LONG-LIFE-COOKIE'
}
