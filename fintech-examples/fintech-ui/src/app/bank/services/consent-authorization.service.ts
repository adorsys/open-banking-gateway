import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { FinTechAuthorizationService } from '../../api';
import { Consent, Payment } from '../../models/consts';
import { StorageService } from '../../services/storage.service';

@Injectable({
  providedIn: 'root'
})
export class ConsentAuthorizationService {
  constructor(
    private router: Router,
    private finTechAuthorizationService: FinTechAuthorizationService,
    private storageService: StorageService
  ) {}

  fromConsent(okOrNotOk: Consent, redirectCode: string) {
    const authId = this.storageService.getRedirectMap().get(redirectCode).authId;
    const xsrfToken = this.storageService.getRedirectMap().get(redirectCode).xsrfToken;
    console.log(
      'ConsentAuthorizationService.fromConsent: pass auth id:' +
        authId +
        ' for ' +
        okOrNotOk +
        ' with redirect code ' +
        redirectCode
    );
    this.finTechAuthorizationService
      .fromConsentGET(authId, okOrNotOk, redirectCode, '', xsrfToken, 'response')
      .subscribe((resp) => {
        console.log(
          'ConsentAuthorizationService.fromConsent: ' +
            'server responded. Now delete redirect cookie for redirect code ',
          redirectCode
        );
        this.storageService.resetRedirectCode(redirectCode);
        const location = resp.headers.get('Location');

        // this is added to handle url where to forward after redirection
        // to be removed when issue https://github.com/adorsys/open-banking-gateway/issues/848 is resolved
        // or Fintech UI refactored
        if (this.storageService.getUserRedirected) {
          // we use the redirect url from the Fintech server when we are redirected back
          this.storageService.setUserRedirected(false);
        }

        this.storageService.deleteSettings();

        console.log('changed settings');
        console.log(
          'ConsentAuthorizationService.fromConsent: location (from response header) to navigate to is now:',
          location
        );

        this.storageService.setAfterRedirect(true);

        this.router.navigate([location]);
      });
  }

  fromPayment(okOrNotOk: Payment, redirectCode: string) {
    const authId = this.storageService.getRedirectMap().get(redirectCode).authId;
    const xsrfToken = this.storageService.getRedirectMap().get(redirectCode).xsrfToken;
    console.log(
      'ConsentAuthorizationService.fromPayment: pass auth id:' +
        authId +
        ' okOrNotOk ' +
        okOrNotOk +
        ' redirect code ' +
        redirectCode
    );
    this.finTechAuthorizationService
      .fromPaymentGET(authId, okOrNotOk, redirectCode, '', xsrfToken, 'response')
      .subscribe((resp) => {
        console.log(
          'ConsentAuthorizationService.fromPayment: server responded. now delete redirect cookie for redirect code ',
          redirectCode
        );
        this.storageService.resetRedirectCode(redirectCode);
        this.router.navigate([resp.headers.get('Location')]);
      });
  }
}
