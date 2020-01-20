import { TestBed } from '@angular/core/testing';

import { BankSearchService } from './bank-search.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('BankSearchService', () => {
  let httpTestingController: HttpTestingController;
  let bankSearchService: BankSearchService;
  const API_PATH = 'fintech-api-proxy'; // TODO: refactor and use `${environment.fintechApi}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BankSearchService]
    });

    httpTestingController = TestBed.get(HttpTestingController);
    bankSearchService = TestBed.get(BankSearchService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(bankSearchService).toBeTruthy();
  });

  it('should test searchBanks method', () => {
    // login credential is not correct
    const keyword = 'deutsche';
    bankSearchService.searchBanks(keyword).subscribe();

    const req = httpTestingController.expectOne(API_PATH + '/v1/banks/fts?q=deutsche&max_results=5');
    expect(req.request.params.get('q')).toEqual('deutsche');
    expect(req.request.params.get('max_results')).toEqual('5');
    expect(req.cancelled).toBeFalsy();
    expect(req.request.method).toEqual('GET');
    req.flush([]);
  });
});
