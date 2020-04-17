import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PsuAuthenticationService } from '../api-auth';
import * as uuid from 'uuid';
import { PsuAuthBody } from '../api-auth';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private psuAuthService: PsuAuthenticationService,
    private sessionService: SessionService
  ) {}

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
