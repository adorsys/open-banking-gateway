import { TestBed } from '@angular/core/testing';

import { BankProfileService } from './bank-profile.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BankSearchService } from './bank-search.service';

describe('BankProfileService', () => {
  let httpTestingController: HttpTestingController;
  let bankProfileService: BankProfileService;
  const API_PATH = 'fintech-api-proxy'; // TODO: refactor and use `${environment.fintechApi}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BankSearchService]
    });

    httpTestingController = TestBed.get(HttpTestingController);
    bankProfileService = TestBed.get(BankProfileService);
  });

  it('should be created', () => {
    const service: BankProfileService = TestBed.get(BankProfileService);
    expect(service).toBeTruthy();
  });
});
