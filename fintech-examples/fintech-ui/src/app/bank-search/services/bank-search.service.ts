import { Injectable } from '@angular/core';
import { FinTechBankSearchService } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class BankSearchService {
  constructor(private finTechBankSearchService: FinTechBankSearchService) {}

  searchBanks(keyword: string) {
    // required headers are set in http interceptor
    // that's the reason for empty strings
    return this.finTechBankSearchService.bankSearchGET('', '', keyword);
  }
}
