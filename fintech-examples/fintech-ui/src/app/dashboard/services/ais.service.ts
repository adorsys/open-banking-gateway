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
          // TODO has to be tidied up
          return [];
        case 200:
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
