import { TestBed } from '@angular/core/testing';

import { BankSearchService } from './bank-search.service';

describe('BankSearchService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BankSearchService = TestBed.get(BankSearchService);
    expect(service).toBeTruthy();
  });
});
