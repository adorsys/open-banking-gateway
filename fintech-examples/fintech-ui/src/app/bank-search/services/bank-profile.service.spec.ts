import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { BankProfileService } from './bank-profile.service';
import { FinTechBankSearchService } from '../../api';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('BankProfileService', () => {
  let bankProfileService: BankProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        BankProfileService,
        FinTechBankSearchService,
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    });

    bankProfileService = TestBed.inject(BankProfileService);
  });

  it('should be created', () => {
    expect(bankProfileService).toBeTruthy();
  });
});
