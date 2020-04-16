import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { PsuAuthService } from '../api-auth/api/psuAuth.service'
import * as uuid from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private psuAuthService: PsuAuthService
  ) {  }

  public userLogin(formData) {
    const xRequestID = uuid.v4();
    console.log('xRequestID: ', xRequestID);
    return this.psuAuthService.login(xRequestID ,formData, 'response');
  }
  public userRegister(formData) {
    const xRequestID = uuid.v4();
    console.log('xRequestID: ', xRequestID);
    return this.psuAuthService.registration(xRequestID ,formData, 'response');
  }
}
