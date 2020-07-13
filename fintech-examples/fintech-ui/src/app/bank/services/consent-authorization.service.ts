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
      let location = resp.headers.get('Location');

      // FIXME: https://github.com/adorsys/open-banking-gateway/issues/848
      // ATTENTION: this is a hotfix and it has to be remove as soon as the ticket #848 is resolved
      if (location.indexOf('account/') > 0 && location.indexOf('payment/') < 0) {
        location = location.substring(0, location.indexOf('account/') + 'account/'.length);
      }
      // ATTENTION: this is a hotfix and it has to be remove as soon as the ticket #848 is resolved

      this.router.navigate([location]);
    });
  }
}
