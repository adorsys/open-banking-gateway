import { Injectable } from '@angular/core';
import { Bank } from '../models/bank.model';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BankSearchService {
  private static BANK_STUBS: Bank[] = [
    { id: '0', name: 'Sparkasse Nürnberg', bic: 'SSXXXX', bankCode: 373737 },
    { id: '1', name: 'Sparkasse Ausburg', bic: 'SSXXXX', bankCode: 473737 },
    { id: '2', name: 'Deutsche Bank', bic: 'SSXYXX', bankCode: 373307 },
    { id: '3', name: 'Sparda Nürnberg', bic: 'SSXXXX', bankCode: 488737 }
  ];

  // path resolved by proxy
  readonly API_PATH = 'fintech-api-proxy';

  constructor(private http: HttpClient) {}

  getBanks(): Observable<Bank[]> {
    return of(BankSearchService.BANK_STUBS).pipe(delay(1000));
  }

  searchBanks(keyword: string): Observable<Bank[]> {
    return this.http.get<Bank[]>(this.API_PATH + '/v1/banks/fts', {
      params: {
        q: keyword,
        max_results: '5'
      }
    });
  }
}
