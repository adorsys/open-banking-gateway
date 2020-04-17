import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  constructor() {}

  public setRedirectCode(authorizationId: string, redirectCode: string) {
    sessionStorage.setItem(authorizationId + Session.REDIRECT_CODE, redirectCode);
  }

  public getRedirectCode(authorizationId: string): string {
    return sessionStorage.getItem(authorizationId + Session.REDIRECT_CODE);
  }

  public setConsentState(authorizationId: string, consentState: any) {
    sessionStorage.setItem(authorizationId + Session.CONSENT_STATE, JSON.stringify(consentState));
  }

  public getConsentState<T>(authorizationId: string, factory: () => T): T {
    if (!sessionStorage.getItem(authorizationId + Session.CONSENT_STATE)) {
      return null;
    }

    return Object.assign(factory(), JSON.parse(sessionStorage.getItem(authorizationId + Session.CONSENT_STATE)));
  }

  public setConsentObject(authorizationId: string, consentObject: any) {
    sessionStorage.setItem(authorizationId + Session.CONSENT_OBJECT, JSON.stringify(consentObject));
  }

  public getConsentObject<T>(authorizationId: string, factory: () => T): T {
    if (!sessionStorage.getItem(authorizationId + Session.CONSENT_OBJECT)) {
      return null;
    }

    return Object.assign(factory(), JSON.parse(sessionStorage.getItem(authorizationId + Session.CONSENT_OBJECT)));
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
  XSRF_TOKEN = 'XSRF_TOKEN'
}
