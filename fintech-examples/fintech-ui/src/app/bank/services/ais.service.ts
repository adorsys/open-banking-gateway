import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { FinTechAccountInformationService } from '../../api';
import { ActivatedRoute } from '@angular/router';
//
@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(
    private route: ActivatedRoute,
    private finTechAccountInformationService: FinTechAccountInformationService
  ) {}

  getAccounts(bankId: string) {
    const okurl = window.location.pathname;
    console.log('redirect url:' + okurl);
    return this.finTechAccountInformationService.aisAccountsGET(bankId, '', '', okurl, 'not-ok-url', 'response').pipe(
      map(response => {
        switch (response.status) {
          case 202:
            const additionalParameters = new URLSearchParams({
              authorizationSessionId: response.headers.get('Authorization-Session-ID'),
              serviceSessionId: response.headers.get('Service-Session-ID'),
              redirectCode: response.headers.get('Redirect-Code')
            });
            window.location.href = response.headers.get('location') + '&' + additionalParameters;
            break;
          case 200:
            console.log('I got the accounts and I want to show them ;-)');
            console.log('I got ', response.body.accounts.length, ' accounts');
            return response.body.accounts;
        }
      })
    );
  }

  getTransactions(bankId: string, accountId: string) {
    return this.finTechAccountInformationService
      .aisTransactionsGET('bankId', 'accountId', '', '', window.location.pathname, 'not-ok-url')
      .pipe(map(response => response.transactions));
  }
}
