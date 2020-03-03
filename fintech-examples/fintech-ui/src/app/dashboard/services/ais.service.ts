import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { FinTechAccountInformationService } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(private finTechAccountInformationService: FinTechAccountInformationService) {}

  getAccounts(bankId: string) {
    //    return this.finTechAccountInformationService
    //    .aisAccountsGET(bankId, '', '', 'ok-url', 'not-ok-url')
    //      .pipe(map(response => response.accounts));

    const response = this.finTechAccountInformationService.aisAccountsGET(
      bankId,
      '',
      '',
      'ok-url',
      'not-ok-url',
      'response'
    );
    response.subscribe(r => {
      console.log('response status is ' + r.status);
      switch (r.status) {
        case 202:
          let locationForRedirect = r.headers.get('location');
          console.log('redirect to ', locationForRedirect);
          console.log('headerfield Service-Session-ID:', r.headers.get('Service-Session-ID'));
          console.log('headerfield Authorization-Session-ID:', r.headers.get('Authorization-Session-ID'));
          console.log('headerfield Redirect-Code:', r.headers.get('Redirect-Code'));
          console.log('before:', decodeURI(locationForRedirect));

          locationForRedirect +=
            '&authorizationSessionId=' +
            r.headers.get('Authorization-Session-ID') +
            '&serviceSessionId=' +
            r.headers.get('Service-Session-ID') +
            '&redirectCode=' +
            r.headers.get('Redirect-Code');
          console.log('after:', decodeURI(locationForRedirect));
          window.location.href = locationForRedirect;
          // TODO has to be tied up
          return [];
        case 200:
          return r.body.accounts;
      }
    });
  }

  getTransactions() {
    return this.finTechAccountInformationService
      .aisTransactionsGET('bankId', 'accountId', '', '', 'ok-url', 'not-ok-url')
      .pipe(map(response => response.transactions));
  }
}
