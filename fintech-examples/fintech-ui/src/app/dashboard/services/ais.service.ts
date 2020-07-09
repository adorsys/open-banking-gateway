import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { FinTechAccountInformationService } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(private finTechAccountInformationService: FinTechAccountInformationService) {}

  getAccounts() {
    return this.finTechAccountInformationService
      .aisAccountsGET('bankId', '', '')
      .pipe(map(response => response.accountList));
  }

  getTransactions() {
    return this.finTechAccountInformationService
      .aisTransactionsGET('bankId', 'accountId', '', '')
      .pipe(map(response => response.accountList));
  }
}
