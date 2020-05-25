import { FinTechAuthorizationService } from '../../api';
import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Consent, HeaderConfig } from '../../models/consts';
import { StorageService } from '../../services/storage.service';

@Injectable({
  providedIn: 'root'
})
export class ConsentAuthorizationService {
  constructor(
    private router: Router,
    private authService: FinTechAuthorizationService,
    private storageService: StorageService
  ) {}

  fromConsentOk(okOrNotOk: Consent, redirectCode: string) {
    const authId = this.storageService.getRedirectMap().get(redirectCode).authId;
    const xsrfToken = this.storageService.getRedirectMap().get(redirectCode).xsrfToken;
    console.log('pass auth id:' + authId + ' okOrNotOk ' + okOrNotOk + ' redirect code ' + redirectCode);
    this.authService.fromConsentGET(authId, okOrNotOk, redirectCode, '', xsrfToken, 'response').subscribe(resp => {
      console.log('fromConsent has returned. now delete redirect cookie for redirect code', redirectCode);
      this.storageService.resetRedirectCode(redirectCode);
      this.router.navigate([resp.headers.get('Location')]);
    });
  }
}
