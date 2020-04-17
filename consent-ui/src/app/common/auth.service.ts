import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PsuAuthBody, PsuAuthenticationAndConsentApprovalService, PsuAuthenticationService } from '../api-auth';
import * as uuid from 'uuid';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private psuAuthService: PsuAuthenticationService,
    private psuAuthForConsentApproval: PsuAuthenticationAndConsentApprovalService,
    private sessionService: SessionService
  ) {}

  public userLoginForConsent(authorizationId: string, redirectCode: string, credentials: PsuAuthBody) {
    const xRequestID = uuid.v4();
    return this.psuAuthForConsentApproval.loginForApproval(xRequestID, authorizationId, redirectCode, credentials, 'response');
  }

  public userLogin(credentials: PsuAuthBody) {
    const xRequestID = uuid.v4();
    return this.psuAuthService.login(xRequestID, credentials, 'response');
  }
  public userRegister(credentials: PsuAuthBody) {
    const xRequestID = uuid.v4();
    return this.psuAuthService.registration(xRequestID, credentials, 'response');
  }
  public isLoggedIn() {
    return this.sessionService.getXsrfToken() !== null;
  }
}
