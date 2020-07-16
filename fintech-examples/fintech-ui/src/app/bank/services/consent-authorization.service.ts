import { FinTechAuthorizationService } from '../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Consent, Payment } from '../../models/consts';
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

      // this is added to handle url where to forward after redirection
      // to be removed when issue https://github.com/adorsys/open-banking-gateway/issues/848 is resolved
      // or Fintech UI refactored
      if (this.storageService.isUserRedirected) {
        // we use the redirect url from the Fintech server when we are redirected back
        this.storageService.isUserRedirected = false;
        this.router.navigate([location]);
      } else {
        // otherwise we use url saved before redirection occurred
        this.router.navigate([this.storageService.redirectCancelUrl]);
      }
    });
  }

  fromPaymentOk(okOrNotOk: Payment, redirectCode: string) {
    const authId = this.storageService.getRedirectMap().get(redirectCode).authId;
    const xsrfToken = this.storageService.getRedirectMap().get(redirectCode).xsrfToken;
    console.log('pass auth id:' + authId + ' okOrNotOk ' + okOrNotOk + ' redirect code ' + redirectCode);
    this.authService.fromPaymentGET(authId, okOrNotOk, redirectCode, '', xsrfToken, 'response').subscribe(resp => {
      console.log('fromPayment has returned. now delete redirect cookie for redirect code', redirectCode);
      this.storageService.resetRedirectCode(redirectCode);
      this.router.navigate([resp.headers.get('Location')]);
    });
  }
}
