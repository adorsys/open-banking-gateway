import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { AccountDetails, AccountList, FinTechAccountInformationService } from '../../api';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AisService {
  private okurl = '';

  constructor(
    private route: ActivatedRoute,
    private finTechAccountInformationService: FinTechAccountInformationService
  ) {
    this.okurl = window.location.protocol + '//' + window.location.host + '/redirectAfterConsent';
    console.log('redirect url:' + this.okurl);
  }

  getAccounts(bankId: string) {
    // TODO maybe without protocol

    return this.finTechAccountInformationService
      .aisAccountsGET(bankId, '', '', this.okurl, 'not-ok-url', 'response')
      .pipe(
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
      .aisTransactionsGET('bankId', 'accountId', '', '', this.okurl, 'not-ok-url')
      .pipe(map(response => response.transactions));
  }
}
