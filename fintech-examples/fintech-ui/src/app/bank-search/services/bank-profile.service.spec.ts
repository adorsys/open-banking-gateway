import { TestBed } from '@angular/core/testing';

import { BankProfileService } from './bank-profile.service';

describe('BankProfileService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BankProfileService = TestBed.get(BankProfileService);
    expect(service).toBeTruthy();
  });
});
