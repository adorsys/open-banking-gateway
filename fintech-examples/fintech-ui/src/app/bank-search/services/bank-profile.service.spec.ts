import { inject, TestBed } from '@angular/core/testing';

import { BankProfileService } from './bank-profile.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FinTechBankSearchService } from '../../api';

describe('BankProfileService', () => {
  let finTechBankSearchService: FinTechBankSearchService;
  let bankProfileService: BankProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BankProfileService, FinTechBankSearchService]
    });

    bankProfileService = TestBed.get(BankProfileService);
    finTechBankSearchService = TestBed.get(FinTechBankSearchService);
  });

  it('should be created', inject([BankProfileService], (service: BankProfileService) => {
    expect(service).toBeTruthy();
  }));
});
