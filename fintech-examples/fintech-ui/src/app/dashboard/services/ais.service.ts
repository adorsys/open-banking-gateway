import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { FinTechAccountInformationService } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(private finTechAccountInformationService: FinTechAccountInformationService) {}

  getAccounts(bankId: string) {
    return this.finTechAccountInformationService
      .aisAccountsGET(bankId, '', '', 'ok-url', 'not-ok-url')
      .pipe(map(response => response.accounts));
  }

  getTransactions() {
    return this.finTechAccountInformationService
      .aisTransactionsGET('bankId', 'accountId', '', '', 'ok-url', 'not-ok-url')
      .pipe(map(response => response.transactions));
  }
}
