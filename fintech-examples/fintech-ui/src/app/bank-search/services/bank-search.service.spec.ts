import { TestBed } from '@angular/core/testing';

import { BankSearchService } from './bank-search.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FinTechBankSearchService } from '../../api';

describe('BankSearchService', () => {
  let finTechBankSearchService: FinTechBankSearchService;
  let bankSearchService: BankSearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BankSearchService, FinTechBankSearchService]
    });

    bankSearchService = TestBed.get(BankSearchService);
    finTechBankSearchService = TestBed.get(FinTechBankSearchService);
  });

  it('should be created', () => {
    expect(bankSearchService).toBeTruthy();
  });

  it('should test searchBanks method', () => {
    const finTechBankSearchServiceSpy = spyOn(finTechBankSearchService, 'bankSearchGET');
    bankSearchService.searchBanks('deutsche');

    expect(finTechBankSearchServiceSpy).toHaveBeenCalledWith('', '', 'deutsche');
  });
});
