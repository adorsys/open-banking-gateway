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
}

enum Session {
  REDIRECT_CODE = ':REDIRECT_CODE',
}
