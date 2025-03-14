import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { BankSearchService } from './bank-search.service';
import { FinTechBankSearchService } from '../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('BankSearchService', () => {
  let finTechBankSearchService: FinTechBankSearchService;
  let bankSearchService: BankSearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [],
    providers: [BankSearchService, FinTechBankSearchService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
});

    bankSearchService = TestBed.inject(BankSearchService);
    finTechBankSearchService = TestBed.inject(FinTechBankSearchService);
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
