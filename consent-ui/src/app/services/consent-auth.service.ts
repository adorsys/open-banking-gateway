import { Injectable } from '@angular/core';
import {ConsentAuthorizationService, PsuAuthRequest} from "../api";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ConsentAuthService {

  constructor(private consentAuthorizationService: ConsentAuthorizationService) { }

  // TODO: clarify where do we get xRequestID and X_XSRF_TOKEN from ??
  public updateConsentEmbedded(authId: string, xRequestID: string, X_XSRF_TOKEN: string, redirectCode?: string, psuAuthRequest?: PsuAuthRequest) : Observable<any> {
    return  this.consentAuthorizationService.embeddedUsingPOST(authId, xRequestID, X_XSRF_TOKEN, redirectCode, psuAuthRequest);
  }

}
