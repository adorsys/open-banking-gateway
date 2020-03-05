import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { FinTechAccountInformationService } from '../../api';
import { ActivatedRoute } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(
    private route: ActivatedRoute,
    private finTechAccountInformationService: FinTechAccountInformationService
  ) {}

  getAccounts(bankId: string) {
    //    return this.finTechAccountInformationService
    //    .aisAccountsGET(bankId, '', '', 'ok-url', 'not-ok-url')
    //      .pipe(map(response => response.accounts));

    // TODO maybe without protocol
    const okurl = window.location.protocol + '//' + window.location.host + '/redirectAfterConsent';
    console.log('redirect url:' + okurl);

    const response = this.finTechAccountInformationService.aisAccountsGET(
      bankId,
      '',
      '',
      okurl,
      'not-ok-url',
      'response'
    );
    response.subscribe(r => {
      console.log('response status is ' + r.status);
      switch (r.status) {
        case 202:
          let locationForRedirect = r.headers.get('location');
          const additionalParameters = new URLSearchParams({
            authorizationSessionId: r.headers.get('Authorization-Session-ID'),
            serviceSessionId: r.headers.get('Service-Session-ID'),
            redirectCode: r.headers.get('Redirect-Code')
          });
          locationForRedirect += '&' + additionalParameters;
          window.location.href = locationForRedirect;
          return []; /* this code is never reached */
        case 200:
          console.log('I got the accounts and I want to show them ;-)');
          console.log('I got ', r.body.accounts.length, ' accounts');
          return r.body.accounts;
      }
    });
    return [];
  }

  getTransactions() {
    return this.finTechAccountInformationService
      .aisTransactionsGET('bankId', 'accountId', '', '', 'ok-url', 'not-ok-url')
      .pipe(map(response => response.transactions));
  }
}
