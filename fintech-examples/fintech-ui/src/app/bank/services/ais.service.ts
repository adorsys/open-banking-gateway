import { Injectable } from '@angular/core';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import { FinTechAccountInformationService } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(private finTechAccountInformationService: FinTechAccountInformationService) {}

  private static isoDate(toConvert: Date) {
    return toConvert.toISOString().split('T')[0];
  }

  getAccounts(bankId: string, loARetrievalInformation: LoARetrievalInformation, withBalance: boolean) {
    const okurl = window.location.pathname;
    const notOkUrl = okurl.replace(/account.*/, '');

    return this.finTechAccountInformationService.aisAccountsGET(
      bankId,
      '',
      '',
      okurl,
      notOkUrl,
      loARetrievalInformation,
      withBalance,
      'response'
    );
  }

  getTransactions(bankId: string, accountId: string, loTRetrievalInformation: LoTRetrievalInformation) {
    const okurl = window.location.pathname;
    const notOkUrl = okurl.replace(/account.*/, 'accounts');

    return this.finTechAccountInformationService.aisTransactionsGET(
      bankId,
      accountId,
      '',
      '',
      okurl,
      notOkUrl,
      loTRetrievalInformation,
      '1970-01-01',
      AisService.isoDate(new Date()),
      null,
      'both',
      null,
      'response'
    );
  }
}
