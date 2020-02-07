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
      .aisAccountsGET(bankId, '', '')
      .pipe(map(response => response.accountList));
  }

  getTransactions() {
    return this.finTechAccountInformationService
      .aisTransactionsGET('bankId', 'accountId', '', '')
      .pipe(map(response => response.accountList));
  }
}
