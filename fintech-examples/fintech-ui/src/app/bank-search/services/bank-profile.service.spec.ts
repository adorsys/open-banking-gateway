import { TestBed } from '@angular/core/testing';

import { BankProfileService } from './bank-profile.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
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

  it('should be created', () => {
    const service: BankProfileService = TestBed.get(BankProfileService);
    expect(service).toBeTruthy();
  });
});
