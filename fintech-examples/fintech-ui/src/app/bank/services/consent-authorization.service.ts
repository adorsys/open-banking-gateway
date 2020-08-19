import { FinTechAuthorizationService } from '../../api';
import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Consent, LoARetrievalInformation, LoTRetrievalInformation, Payment } from '../../models/consts';
import { StorageService } from '../../services/storage.service';
import { SettingsService } from './settings.service';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConsentAuthorizationService {
  constructor(
    private router: Router,
    private finTechAuthorizationService: FinTechAuthorizationService,
    private storageService: StorageService,
    private settingsService: SettingsService
  ) {
  }

  fromConsent(okOrNotOk: Consent, redirectCode: string) {
    const authId = this.storageService.getRedirectMap().get(redirectCode).authId;
    const xsrfToken = this.storageService.getRedirectMap().get(redirectCode).xsrfToken;
    console.log('ConsentAuthorizationService.fromConsent: pass auth id:' + authId + ' for ' + okOrNotOk + ' with redirect code '
      + redirectCode);
    this.finTechAuthorizationService.fromConsentGET(authId, okOrNotOk, redirectCode, '', xsrfToken,
      'response').subscribe(resp => {
      console.log('ConsentAuthorizationService.fromConsent: ' +
        'server responded. Now delete redirect cookie for redirect code ', redirectCode);
      this.storageService.resetRedirectCode(redirectCode);
      const location = resp.headers.get('Location');

      // this is added to handle url where to forward after redirection
      // to be removed when issue https://github.com/adorsys/open-banking-gateway/issues/848 is resolved
      // or Fintech UI refactored
      if (this.storageService.isUserRedirected) {
        // we use the redirect url from the Fintech server when we are redirected back
        this.storageService.isUserRedirected = false;
      }
      this.settingsService.setLoA(LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT);
      this.settingsService.setLoT(LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT);
      console.log('changed settings');
      console.log('ConsentAuthorizationService.fromConsent: location (from response header) to navigate to is now:', location);
      this.router.navigate([location]);
    });
  }

  fromPayment(okOrNotOk: Payment, redirectCode: string) {
    const authId = this.storageService.getRedirectMap().get(redirectCode).authId;
    const xsrfToken = this.storageService.getRedirectMap().get(redirectCode).xsrfToken;
    console.log('ConsentAuthorizationService.fromPayment: pass auth id:' + authId + ' okOrNotOk ' + okOrNotOk + ' redirect code '
      + redirectCode);
    this.finTechAuthorizationService.fromPaymentGET(authId, okOrNotOk, redirectCode, '', xsrfToken, 'response')
      .subscribe(resp => {
        console.log('ConsentAuthorizationService.fromPayment: server responded. now delete redirect cookie for redirect code ',
          redirectCode);
        this.storageService.resetRedirectCode(redirectCode);
        this.router.navigate([resp.headers.get('Location')]);
      });
  }
}
