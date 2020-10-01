import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { BankProfileService } from './bank-profile.service';
import { FinTechBankSearchService } from '../../api';

describe('BankProfileService', () => {
  let finTechBankSearchService: FinTechBankSearchService;
  let bankProfileService: BankProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BankProfileService, FinTechBankSearchService]
    });

    bankProfileService = TestBed.inject(BankProfileService);
    finTechBankSearchService = TestBed.inject(FinTechBankSearchService);
  });

  it('should be created', () => {
    expect(bankProfileService).toBeTruthy();
  });
});
