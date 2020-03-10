import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  constructor() { }

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
    return Object.assign(
      factory(),
      JSON.parse(sessionStorage.getItem(authorizationId + Session.CONSENT_STATE))
    );
  }

  public setConsentObject(authorizationId: string, consentObject: any) {
    sessionStorage.setItem(authorizationId + Session.CONSENT_OBJECT, JSON.stringify(consentObject));
  }

  public getConsentObject<T>(authorizationId: string, factory: () => T): T {
    console.log(authorizationId + Session.CONSENT_OBJECT)
    console.log(sessionStorage.getItem(authorizationId + Session.CONSENT_OBJECT))
    return Object.assign(
      factory(),
      JSON.parse(sessionStorage.getItem(authorizationId + Session.CONSENT_OBJECT))
    );
  }
}

enum Session {
  REDIRECT_CODE = ':REDIRECT_CODE',
  CONSENT_STATE = ':CONSENT_STATE',
  CONSENT_OBJECT = ':CONSENT_OBJECT',
}
